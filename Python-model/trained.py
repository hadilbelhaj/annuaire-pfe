import pandas as pd
import joblib
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor


df = pd.read_csv("output.csv", encoding="utf-8")

df = df[['amount', 'specialtyAverageAmount', 'medicalSpecialty', 'designation', 'reimbursementPercentage']]


print("Missing values:\n", df.isnull().sum())

le_specialty = LabelEncoder()
le_designation = LabelEncoder()

df['medicalSpecialty'] = le_specialty.fit_transform(df['medicalSpecialty'])
df['designation'] = le_designation.fit_transform(df['designation'])
print(df.head())

X = df.drop(columns=['reimbursementPercentage'])
y = df['reimbursementPercentage']

# Splitting the dataset into 80% training and 20% testing
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

print(f"Training samples: {len(X_train)}, Testing samples: {len(X_test)}")

# Initialize and train the model
model = RandomForestRegressor(n_estimators=100, random_state=42)
model.fit(X_train, y_train)

# Save model and encoders
joblib.dump(model, 'fraud_detection_model.joblib')
joblib.dump(le_specialty, 'le_specialty.joblib')
joblib.dump(le_designation, 'le_designation.joblib')

print("Model and encoders saved successfully!")
