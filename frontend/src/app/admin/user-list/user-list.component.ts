import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {  User } from '../../models/user';
import { UserService } from '../../services/user.service';

export enum Role {
  USER = 'USER',
  ADMIN = 'ADMIN'
}


@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css'
})
export class UserListComponent implements OnInit, OnDestroy {
  users: User[] = [];
  selectedUser: User | null = null;
  userForm: FormGroup;
  isEditing = false;
  showForm = false;
  roles = [Role.USER, Role.ADMIN];
  loading = false;
  errorMessage = '';
showDeleteConfirmation = false;
userToDelete: User | null = null;


  constructor(
    private userService: UserService,
    private fb: FormBuilder
  ) {
    this.userForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', this.isEditing ? [] : [Validators.required, Validators.minLength(6)]],
      role: [Role.USER, Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      address: [''],
    });
  }
  ngOnDestroy(): void {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getUsers().subscribe({
      next: (data) => {
        this.users = data.filter(user => user.role !== 'super-admin');
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to load users: ' + error.message;
        this.loading = false;
      }
    });
  }

  addNewUser(): void {
    this.isEditing = false;
    this.selectedUser = null;
    this.resetForm();
    this.showForm = true;
  }

  editUser(user: User): void {
    this.isEditing = true;
    this.selectedUser = user;
    
    this.userForm.patchValue({
      email: user.email,
      role: user.role,
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      address: user.address || '',
    });
    
    this.userForm.get('password')?.setValidators([]);
    this.userForm.get('password')?.updateValueAndValidity();
    
    this.showForm = true;
  }

  saveUser(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    const userData = this.userForm.value as User;
    this.loading = true;

    if (this.isEditing && this.selectedUser?.id) {
      this.userService.updateUser(this.selectedUser.id, userData).subscribe({
        next: () => {
          this.loadUsers();
          this.resetForm();
          this.loading = false;
          this.showForm = false;
        },
        error: (error) => {
          this.errorMessage = 'Failed to update user: ' + error.message;
          this.loading = false;
        }
      });
    } else {
      this.userService.createUser(userData).subscribe({
        next: () => {
          this.loadUsers();
          this.resetForm();
          this.loading = false;
          this.showForm = false;
        },
        error: (error) => {
          this.errorMessage = 'Failed to create user: ' + error.message;
          this.loading = false;
        }
      });
    }
  }

  deleteUser(user: User): void {
    if (!user.id) return;
    
    this.loading = true;
    this.userService.deleteUser(user.id).subscribe({
      next: () => {
        this.loadUsers();
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to deactivate user: ' + error.message;
        this.loading = false;
      }
    });
  }
  isUserDeleted(user: User): boolean {
    return user.deleted === 1;
  }

  restoreUser(user: User): void {
    if (!user.id) return;
    
    this.loading = true;
    this.userService.restoreUser(user.id).subscribe({
      next: () => {
        this.loadUsers();
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Failed to restore user: ' + error.message;
        this.loading = false;
      }
    });
  }

  resetForm(): void {
    this.userForm.reset({
      role: Role.USER
    });
    
    // Reset password validator if needed
    if (!this.isEditing) {
      this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
      this.userForm.get('password')?.updateValueAndValidity();
    }
  }

  cancelEdit(): void {
    this.resetForm();
    this.showForm = false;
  }

  clearError(): void {
    this.errorMessage = '';
  }
generateColor(str: string): string {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = str.charCodeAt(i) + ((hash << 5) - hash);
  }
  
  const h = Math.abs(hash) % 360;
  const s = 65 + Math.abs(hash % 20);
  const l = 45 + Math.abs(hash % 10);
  
  return `hsl(${h}, ${s}%, ${l}%)`;
}
confirmDelete(user: User): void {
  this.userToDelete = user;
  this.showDeleteConfirmation = true;
}

cancelDelete(): void {
  this.showDeleteConfirmation = false;
  this.userToDelete = null;
}

confirmDeleteUser(): void {
  if (this.userToDelete) {
    this.deleteUser(this.userToDelete);
    this.showDeleteConfirmation = false;
    this.userToDelete = null;
  }
}
}
