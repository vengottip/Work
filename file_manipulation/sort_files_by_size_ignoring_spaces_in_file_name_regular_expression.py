import pandas as pd
import re

# Define the columns names
column_names = ['permissions', 'number', 'user', 'group', 'size', 'Date', 'Time', 'file_name']

# Create a list to store the rows
rows = []

# Open the file and parse it manually
with open('your_file.csv', 'r') as file:
    for line in file:
        # Split the line by one or more spaces
        parts = re.split(r'\s+', line.strip())
        
        # Join the content after the 7th index to handle spaces in file_name
        if len(parts) > 7:
            row = parts[:7] + [' '.join(parts[7:])]
        else:
            row = parts
        rows.append(row)

# Print the rows for debugging
for i, row in enumerate(rows):
    print(f"Row {i + 1}: {row} (Number of columns: {len(row)})")

# Ensure that each row has exactly 8 columns
for row in rows:
    if len(row) != 8:
        print("Error: A row does not have exactly 8 columns.")
        print(f"Row: {row}")
        break

# If everything looks correct, create the DataFrame
df = pd.DataFrame(rows, columns=column_names)

# Convert 'size' column to numeric, ignoring errors (in case of non-numeric values)
df['size'] = pd.to_numeric(df['size'], errors='coerce')

# Sort the DataFrame by size
sorted_df = df.sort_values(by='size', ascending=False)

# Save the sorted data to a new file
sorted_df.to_csv('sorted_files.csv', sep='\t', index=False)
