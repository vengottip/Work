import cx_Oracle
import pandas as pd

# Load the CSV file into a DataFrame
metadata_df = pd.read_csv('path_to_your_csv_file.csv', dtype={'PK_COL_VAL': 'Int64'})

# Set up Oracle connection (replace with your actual credentials)
connection = cx_Oracle.connect(user="your_username", password="your_password", dsn="your_dsn")
cursor = connection.cursor()

# Create a DataFrame to store results
results_df = pd.DataFrame(columns=['TBL_NM', 'COL_NM', 'PK_COL', 'PK_COL_VAL', 'RESULT'])

# Loop through each row in the CSV
for index, row in metadata_df.iterrows():
    table_name = row['TBL_NM']
    column_name = row['COL_NM']
    pk_column = row['PK_COL']
    pk_value = int(row['PK_COL_VAL'])  # Ensures pk_value is treated as a full integer

    # Construct the query to get data from tables in schema B, selecting only the specific column
    query = f"SELECT {column_name} FROM B.{table_name} WHERE {pk_column} = :pk_value"

    # Execute the query with the primary key value as a bind variable
    cursor.execute(query, pk_value=pk_value)
    records = cursor.fetchall()
    
    # Append each record to the DataFrame
    for record in records:
        results_df = results_df.append({
            'TBL_NM': table_name,
            'COL_NM': column_name,
            'PK_COL': pk_column,
            'PK_COL_VAL': pk_value,  # Full integer value preserved
            'RESULT': record[0]
        }, ignore_index=True)

# Write the DataFrame to an Excel file
output_file_path = 'output_records.xlsx'
with pd.ExcelWriter(output_file_path, engine='openpyxl') as writer:
    results_df.to_excel(writer, index=False, sheet_name='Results')

# Close the cursor and connection
cursor.close()
connection.close()

print(f"Data successfully written to {output_file_path}")
