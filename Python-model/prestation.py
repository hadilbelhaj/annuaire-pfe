from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.multioutput import MultiOutputClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import MultiLabelBinarizer
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report
import numpy as np

PRESTATIONS = [
    "Consultation", "Pharmacie", "UU", "AMC", "Analyse", "Ortho",
    "Radio", "SD", "PD", "ODF", "Implant", "Verre", "Lentille",
    "Hospitalisation", "Circoncision", "Maternité", "Chirurgie", "FF", "Transport"
]

SPECIALITES = [
    "Médecine légale", "Médecine de Famille", "Pédiatrie", "Médecine générale",
    "Médecine interne", "Pneumologie", "Chirurgie pédiatrique", "Cardiologie",
    "Chirurgie orthopédique et traumatologique", "Biologie médicale option microbiologie",
    "Oto-rhino-laryngologie", "Maladies infectieuses", "Biologie médicale option parasitologie",
    "Néphrologie", "Gynécologie obstétrique", "Biologie médicale", "Biologie médicale option immunologie",
    "Anesthésie réanimation", "Gastro-entérologie", "Ophtalmologie", "Neurologie", "Psychiatrie",
    "Endocrinologie", "Imagerie médicale", "Médecine d'urgence", "Chirurgie carcinologique",
    "Stomatologie et chirurgie maxillo-faciale", "Radiothérapie carcinologique", "Chirurgie générale",
    "Spécialiste en médecine de famille", "Médecine préventive et communautaire", "Réanimation médicale",
    "Chirurgie thoracique", "Hématologie clinique", "Biologie médicale option biochimie", "sans spécialité",
    "Carcinologie médicale", "Dermatologie", "Chirurgie cardio vasculaire", "Anatomie et cytologie pathologique",
    "Biologie médicale option hématologie", "Génétique", "Chirurgie plastique réparatrice et esthétique",
    "Chirurgie urologique", "Pédo psychiatrie", "Médecine du travail",
    "Médecine physique, rééducation et réadaptation fonctionnelle", "Physiologie et exploration fonctionnelle",
    "Chirurgie neurologique", "Biophysique et médecine nucléaire", "Nutrition et maladies nutritionnelles",
    "Rhumatologie", "Histo-embryologie", "Pharmacologie", "Chirurgie vasculaire périphérique", "Urologie",
    "Médecine aéronautique et spatiale", "Anatomie"
]
rules = [
    {"keywords": ["chirurgie"], "suggestions": ["Chirurgie", "Hospitalisation", "Consultation"]},
    {"keywords": ["pédiatrie", "enfant"], "suggestions": ["Consultation", "Hospitalisation", "Circoncision"]},
    {"keywords": ["gynécologie", "maternité"], "suggestions": ["Maternité", "Consultation", "Hospitalisation"]},
    {"keywords": ["ophtalmologie", "vision"], "suggestions": ["Verre", "Lentille", "Consultation"]},
    {"keywords": ["neurologie", "psychiatrie"], "suggestions": ["Consultation", "Hospitalisation"]},
    {"keywords": ["radio", "imagerie"], "suggestions": ["Radio"]},
    {"keywords": ["médecine générale", "famille"], "suggestions": ["Consultation", "Pharmacie"]},
    {"keywords": ["urgence", "réanimation"], "suggestions": ["Hospitalisation", "Transport"]}
]
specialty_prestation_map = {
    "Médecine légale": ["Consultation"],
    "Médecine de Famille": ["Consultation", "Pharmacie"],
    "Pédiatrie": ["Consultation", "Hospitalisation", "Circoncision"],
    "Médecine générale": ["Consultation", "Pharmacie"],
    "Médecine interne": ["Consultation"],
    "Pneumologie": ["Consultation", "Hospitalisation"],
    "Chirurgie pédiatrique": ["Chirurgie", "Hospitalisation", "Consultation"],
    "Cardiologie": ["Consultation", "Analyse"],
    "Chirurgie orthopédique et traumatologique": ["Chirurgie", "Hospitalisation"],
    "Biologie médicale option microbiologie": ["Analyse"],
    "Oto-rhino-laryngologie": ["Consultation", "Radio"],
    "Maladies infectieuses": ["Consultation", "Hospitalisation"],
    "Biologie médicale option parasitologie": ["Analyse"],
    "Néphrologie": ["Analyse", "Hospitalisation"],
    "Gynécologie obstétrique": ["Maternité", "Consultation", "Hospitalisation"],
    "Biologie médicale": ["Analyse"],
    "Biologie médicale option immunologie": ["Analyse"],
    "Anesthésie réanimation": ["Hospitalisation", "Transport"],
    "Gastro-entérologie": ["Consultation", "Analyse"],
    "Ophtalmologie": ["Verre", "Lentille", "Consultation"],
    "Neurologie": ["Consultation", "Hospitalisation"],
    "Psychiatrie": ["Consultation", "Hospitalisation"],
    "Endocrinologie": ["Consultation", "Analyse"],
    "Imagerie médicale": ["Radio"],
    "Médecine d'urgence": ["Hospitalisation", "Transport"],
    "Chirurgie carcinologique": ["Chirurgie", "Hospitalisation"],
    "Stomatologie et chirurgie maxillo-faciale": ["Chirurgie", "Consultation"],
    "Radiothérapie carcinologique": ["Hospitalisation", "Consultation"],
    "Chirurgie générale": ["Chirurgie", "Hospitalisation"],
    "Spécialiste en médecine de famille": ["Consultation", "Pharmacie"],
    "Médecine préventive et communautaire": ["Consultation"],
    "Réanimation médicale": ["Hospitalisation", "Transport"],
    "Chirurgie thoracique": ["Chirurgie", "Hospitalisation"],
    "Hématologie clinique": ["Analyse"],
    "Biologie médicale option biochimie": ["Analyse"],
    "sans spécialité": ["Consultation", "Pharmacie"],
    "Carcinologie médicale": ["Hospitalisation", "Consultation"],
    "Dermatologie": ["Consultation", "Pharmacie"],
    "Chirurgie cardio vasculaire": ["Chirurgie", "Hospitalisation"],
    "Anatomie et cytologie pathologique": ["Analyse"],
    "Biologie médicale option hématologie": ["Analyse"],
    "Génétique": ["Analyse"],
    "Chirurgie plastique réparatrice et esthétique": ["Chirurgie", "Consultation"],
    "Chirurgie urologique": ["Chirurgie", "Hospitalisation"],
    "Pédo psychiatrie": ["Consultation", "Hospitalisation"],
    "Médecine du travail": ["Consultation"],
    "Médecine physique, rééducation et réadaptation fonctionnelle": ["Hospitalisation", "SD"],
    "Physiologie et exploration fonctionnelle": ["Analyse"],
    "Chirurgie neurologique": ["Chirurgie", "Hospitalisation"],
    "Biophysique et médecine nucléaire": ["Analyse", "Radio"],
    "Nutrition et maladies nutritionnelles": ["Consultation", "Pharmacie"],
    "Rhumatologie": ["Consultation", "Analyse"],
    "Histo-embryologie": ["Analyse"],
    "Pharmacologie": ["Pharmacie"],
    "Chirurgie vasculaire périphérique": ["Chirurgie", "Hospitalisation"],
    "Urologie": ["Consultation", "Chirurgie"],
    "Médecine aéronautique et spatiale": ["Consultation"],
    "Anatomie": ["Analyse"]
}

def apply_rules(specialty):
    suggestions = set()
    for rule in rules:
        if any(k in specialty.lower() for k in rule["keywords"]):
            suggestions.update(rule["suggestions"])
    return list(suggestions)


vectorizer = TfidfVectorizer(max_features=100)
mlb = MultiLabelBinarizer()
def generate_dataset():
    X, y = [], []
    for specialty in SPECIALITES:
        X.append(specialty.lower())
        y.append(apply_rules(specialty))
    return X, y

def train_model():
    X, y = generate_dataset()
    y_bin = mlb.fit_transform(y)
    classes = mlb.classes_
    pos_counts = y_bin.sum(axis=0)
    valid_mask = pos_counts >= 2
    y_bin = y_bin[:, valid_mask]
    classes = classes[valid_mask]
    mlb_filtered = MultiLabelBinarizer(classes=classes)
    y_bin = mlb_filtered.fit_transform(y)
    X_vec = vectorizer.fit_transform(X)
    X_train, X_test, y_train, y_test = train_test_split(
        X_vec, y_bin, test_size=0.2, random_state=42
    )
    # Train multi-label classifier
    model = MultiOutputClassifier(LogisticRegression(max_iter=1000))
    model.fit(X_train, y_train)
    return model, X_test, y_test, mlb_filtered

def predict(model, specialty):
    rule_suggestions = set(apply_rules(specialty))
    X_vec = vectorizer.transform([specialty.lower()])
    probas = model.predict_proba(X_vec)

    ml_preds = set()

    for i, p in enumerate(probas):
        # p is array shape (n_samples, 2); take sample 0, class 1 probability
        pos_prob = p[0][1] if p.shape[1] > 1 else p[0]
        if pos_prob > 0.5:
            ml_preds.add(mlb.classes_[i])

    return list(rule_suggestions.union(ml_preds))

def evaluate(model, X_test, y_test):
    y_pred = model.predict(X_test)
    return classification_report(y_test, y_pred, target_names=mlb.classes_)