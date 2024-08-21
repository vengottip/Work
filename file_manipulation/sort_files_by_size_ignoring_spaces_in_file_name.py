import pandas as pd
import csv

# Define the columns names
column_names = ['permissions', 'number', 'user', 'group', 'size', 'Date', 'Time', 'file_name']

# Create a list to store the rows
rows = []

# Open the file and parse it manually
with open('your_file.csv', 'r') as file:
    reader = csv.reader(file, delimiter='\t')
    for row in reader:
        # Join the content after the 7th index to handle spaces in file_name
        if len(row) > 7:
            row = row[:7] + [' '.join(row[7:])]
        rows.append(row)

# Create a DataFrame from the rows
df = pd.DataFrame(rows, columns=column_names)

# Convert 'size' column to numeric, ignoring errors (in case of non-numeric values)
df['size'] = pd.to_numeric(df['size'], errors='coerce')

# Sort the DataFrame by size
sorted_df = df.sort_values(by='size', ascending=False)

# Save the sorted data to a new file
sorted_df.to_csv('sorted_files.csv', sep='\t', index=False)
