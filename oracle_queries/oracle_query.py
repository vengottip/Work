"""
SELECT TBL_NM, COL_NM, PK_COL, PK_COL_VAL 
FROM A.T1;

"""

import cx_Oracle
import pandas as pd

# Load the CSV file into a DataFrame
metadata_df = pd.read_csv('path_to_your_csv_file.csv')

# Set up Oracle connection (replace with your actual credentials)
connection = cx_Oracle.connect(user="your_username", password="your_password", dsn="your_dsn")
cursor = connection.cursor()

# Loop through each row in the CSV
for index, row in metadata_df.iterrows():
    table_name = row['TBL_NM']
    column_name = row['COL_NM']
    pk_column = row['PK_COL']
    pk_value = row['PK_COL_VAL']

    # Construct the query to get data from tables in schema B
    query = f"SELECT * FROM B.{table_name} WHERE {pk_column} = :pk_value"

    # Execute the query with the primary key value as a bind variable
    cursor.execute(query, pk_value=pk_value)
    records = cursor.fetchall()
    
    # Process the records as needed (print, store, analyze, etc.)
    for record in records:
        print(record)  # or handle as required

# Close the cursor and connection
cursor.close()
connection.close()