import csv

input_file = 'input.csv'
output_file = 'output.csv'

with open(input_file, 'r') as file:
    reader = csv.reader(file)
    header = next(reader)  # Read the header row
    data = list(reader)  # Read the remaining rows

for row in data:
    row[7] = row[7].replace(' ', '')  # Remove spaces from the 8th column

with open(output_file, 'w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(header)  # Write the header row
    writer.writerows(data)  # Write the modified data_