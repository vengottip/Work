import cx_Oracle
import pandas as pd
import csv

# Load the CSV file into a DataFrame
metadata_df = pd.read_csv('path_to_your_csv_file.csv', dtype={'PK_COL_VAL': 'Int64'})

# Set up Oracle connection (replace with your actual credentials)
connection = cx_Oracle.connect(user="your_username", password="your_password", dsn="your_dsn")
cursor = connection.cursor()

# Open a file to write the records
with open('output_records.csv', mode='w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(['TBL_NM', 'COL_NM', 'PK_COL', 'PK_COL_VAL', 'RESULT'])  # Header row

    # Loop through each row in the CSV
    for index, row in metadata_df.iterrows():
        table_name = row['TBL_NM']
        column_name = row['COL_NM']
        pk_column = row['PK_COL']
        pk_value = int(row['PK_COL_VAL'])  # Ensures pk_value is an integer for Oracle query

        # Construct the query to get data from tables in schema B, selecting only the specific column
        query = f"SELECT {column_name} FROM B.{table_name} WHERE {pk_column} = :pk_value"

        # Execute the query with the primary key value as a bind variable
        cursor.execute(query, pk_value=pk_value)
        records = cursor.fetchall()
        
        # Write each record to the CSV file
        for record in records:
            writer.writerow([table_name, column_name, pk_column, pk_value, record[0]])

# Close the cursor and connection
cursor.close()
connection.close()
