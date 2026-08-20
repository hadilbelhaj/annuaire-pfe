from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import rsa, padding
from cryptography.hazmat.primitives import serialization
import base64
import json
import os
from cryptography.exceptions import InvalidSignature

KEY_DIR = "keys"
def load_private_key(user_id):
    with open(os.path.join(KEY_DIR, f"{user_id}_private.pem"), "rb") as f:
        return serialization.load_pem_private_key(f.read(), password=None)

def load_public_key(user_id):
    with open(os.path.join(KEY_DIR, f"{user_id}_public.pem"), "rb") as f:
        return serialization.load_pem_public_key(f.read())

def generate_keypair(societe):
    os.makedirs(KEY_DIR, exist_ok=True)
    private_path = os.path.join(KEY_DIR, f"{societe}_private.pem")
    public_path = os.path.join(KEY_DIR, f"{societe}_public.pem")

    private_key = rsa.generate_private_key(public_exponent=65537, key_size=2048)
    public_key = private_key.public_key()

    with open(private_path, "wb") as f:
        f.write(private_key.private_bytes(
            serialization.Encoding.PEM,
            serialization.PrivateFormat.PKCS8,
            serialization.NoEncryption()
        ))

    with open(public_path, "wb") as f:
        f.write(public_key.public_bytes(
            serialization.Encoding.PEM,
            serialization.PublicFormat.SubjectPublicKeyInfo
        ))

    return True

def sign_data(societe, data_dict, private_key):
    data_json = json.dumps(data_dict, sort_keys=True).encode()
    signature = private_key.sign(
        data_json,
        padding.PSS(mgf=padding.MGF1(hashes.SHA256()), salt_length=padding.PSS.MAX_LENGTH),
        hashes.SHA256()
    )
    return {
        "societe": societe,
        "data": data_dict,
        "signature": base64.b64encode(signature).decode()
    }

def verify_data_qr(payload, public_key):
    try:
        data_json = json.dumps(payload["data"], sort_keys=True).encode()
        signature = base64.b64decode(payload["signature"])

        public_key.verify(
            signature,
            data_json,
            padding.PSS(mgf=padding.MGF1(hashes.SHA256()), salt_length=padding.PSS.MAX_LENGTH),
            hashes.SHA256()
        )
        return True
    except InvalidSignature:
        return False
    except Exception:
        return False

