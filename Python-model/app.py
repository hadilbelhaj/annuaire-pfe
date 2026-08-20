from io import BytesIO
import json
from flask import Flask, request, jsonify, send_file
import pandas as pd
import joblib
from prestation import evaluate,predict,train_model
from qr import generate_keypair,verify_data_qr,sign_data,load_private_key,load_public_key
import qrcode
from PIL import Image
import cv2
import numpy as np
from flask import Flask
from flask_cors import CORS
app = Flask(__name__)
CORS(app, origins=["http://localhost:4200"])


model = joblib.load('fraud_detection_model.joblib')

# Load fraud detection models

fraud_model = joblib.load('fraud_detection_model.joblib')

le_specialty = joblib.load('le_specialty.joblib')
le_designation = joblib.load('le_designation.joblib')

model, X_test, y_test,filtered = train_model()

@app.route('/detect-fraud', methods=['POST'])
def detect_fraud():
    data = request.json
    
   
    new_claim_df = pd.DataFrame([{
        'amount': data['amount'],
        'specialtyAverageAmount': data['specialtyAverageAmount'],
        'medicalSpecialty': data['medicalSpecialty'],
        'designation': data['designation']
    }])
    
  
    new_claim_df['medicalSpecialty'] = le_specialty.transform([data['medicalSpecialty']])
    new_claim_df['designation'] = le_designation.transform([data['designation']])
    
   
    predicted_percentage = model.predict(new_claim_df)[0]
    actual_percentage = data['reimbursementPercentage']
    
    
    threshold = 10
    is_fraudulent = actual_percentage > predicted_percentage + threshold
    try:
        data = request.get_json()
        df = pd.DataFrame([{
            'amount': float(data['amount']),
            'specialtyAverageAmount': float(data['specialtyAverageAmount']),
            'medicalSpecialty': le_specialty.transform([data['medicalSpecialty']])[0],
            'designation': le_designation.transform([data['designation']])[0]
        }])
        
        predicted = float(fraud_model.predict(df)[0])
        actual = float(data['reimbursementPercentage'])
        
        return jsonify({
            'isFraudulent': actual > predicted + 10,
            'predictedPercentage': round(predicted, 2),
            'actualPercentage': actual,
            'threshold': 10
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 400

@app.route("/create-societe", methods=["POST"])
def create_user():
    societe = request.json.get("societe")
    if not societe:
        return jsonify({"error": "Missing societe"}), 400
    try:
        generate_keypair(societe)
        return jsonify({"message": f"User '{societe}' created with key pair."})
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/verify-qr-image", methods=["POST"])
def verify_qr_image():
    if 'file' not in request.files:
        return jsonify({"error": "No file uploaded"}), 400
        
    file = request.files['file']
    try:
        # Read and decode image
        filestr = file.read()
        img = Image.open(file.stream)
        decoded = decode(img)
        if not decoded:
            return jsonify({"error": "No QR code found"}), 400
        qr_data = decoded[0]
        payload = json.loads(qr_data.data.decode('utf-8'))

        # Verify required fields
        if "societe" not in payload:
            return jsonify({"error": "Missing societe in QR data"}), 400
            
        # Load public key and verify
        public_key = load_public_key(payload["societe"])
        valid = verify_data_qr(payload, public_key)
        
        return jsonify({
            "valid": valid,
            "societe": payload["societe"],
            "data": payload.get("data"),
            "timestamp": payload.get("timestamp")
        })
        
    except json.JSONDecodeError:
        return jsonify({"error": "Invalid QR code data format"}), 400
    except Exception as e:
        return jsonify({"error": f"Verification failed: {str(e)}"}), 500
    
@app.route("/generate-qr", methods=["POST"])
def generate_qr():
    user_id = request.json.get("societe")
    data = request.json.get("data")
    if not user_id or not data:
        return jsonify({"error": "Missing societe or data"}), 400

    try:
        private_key = load_private_key(user_id)
        signed_payload = sign_data(user_id, data, private_key)
        qr = qrcode.QRCode(
            version=1,
            error_correction=qrcode.constants.ERROR_CORRECT_H,
            box_size=10,
            border=4,
        )
        qr.add_data(json.dumps(signed_payload))
        qr.make(fit=True)
        img = qr.make_image(fill_color="black", back_color="white")
        img_buffer = BytesIO()
        img.save(img_buffer, format="PNG")
        img_buffer.seek(0)
        
        return send_file(img_buffer, mimetype="image/png")
        
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route("/evaluate", methods=["GET"])
def evaluate_endpoint():
    report = evaluate(model, X_test, y_test)
    return f"<pre>{report}</pre>"

@app.route("/predict", methods=["GET"])
def predict_endpoint():
    specialty = request.args.get("specialty", "")
    if not specialty:
        return jsonify({"error": "Missing 'specialty' param"}), 400

    
    suggestions = predict(model, specialty)
    return jsonify({"specialty": specialty, "suggestions": suggestions})

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({
        "status": "ok",
        "prestation_model": "loaded",
        "fraud_model": "loaded"
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)