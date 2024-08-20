import csv

# Open the input CSV file
with open('input.csv', 'r') as file:
    reader = csv.reader(file)
    rows = list(reader)

# Add headers for the 9th and 10th columns
rows[0].extend(['Size (TB)', 'Cum. size (TB)'])

# Iterate over the rows and add values to the 9th and 10th columns
cumulative_value = 0
for row in rows[1:]:
    # Calculate the value for the 9th column
    value_9th = float(row[4]) / (2 ** 40)
    row.append(value_9th)

    # Calculate the cumulative value for the 10th column
    cumulative_value += value_9th
    row.append(cumulative_value)

# Write the modified rows to a new CSV file
with open('output.csv', 'w', newline='') as file:
    writer = csv.writer(file)
    writer.writerows(rows)