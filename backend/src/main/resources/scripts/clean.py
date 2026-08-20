import json
import sys
import pandas as pd 
from datetime import datetime
def clean_data(input_path, output_folder):
    with open(input_path, 'r' , encoding='utf-8') as f:
        data = json.load(f) 
    df= pd.DataFrame(data)
    df = df[df['ModeExercice'] != 'Décédé']
    df = df.replace(r'\n', ' ', regex=True)
    df = df.replace(r'\s+', ' ', regex=True)
    df['VilleAdresseCourrier'] = df['VilleAdresseCourrier'].str.replace(r'""', '"', regex=True)
    df['VilleAdresseCourrier'] = df['VilleAdresseCourrier'].str.strip('"')
    df['Telephone'] = df['Telephone'].str.replace(' ', '', regex=False)
    df['TelephonePortable'] = df['TelephonePortable'].str.replace(' ', '', regex=False)
    today_date = datetime.today().strftime('%Y-%m-%d')
    output_file = f"{output_folder}/clean_{today_date}.csv"
    df.to_csv(output_file, index=False)
    print("Saved data to : ", output_file)
if __name__ == "__main__":
    input_path = sys.argv[1]
    output_path = sys.argv[2]
    clean_data(input_path, output_path)