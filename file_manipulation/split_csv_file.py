import csv

def split_csv(input_file, output_prefix, chunk_size):
    with open(input_file, 'r') as file:
        reader = csv.reader(file)
        header = next(reader)  # Assuming the first row is the header

        chunk_count = 1
        records = []
        for row in reader:
            records.append(row)
            if len(records) == chunk_size:
                output_file = f"{output_prefix}_{chunk_count}.csv"
                with open(output_file, 'w', newline='') as output:
                    writer = csv.writer(output)
                    writer.writerow(header)
                    writer.writerows(records)
                chunk_count += 1
                records = []

        # Write any remaining records to the last file
        if records:
            output_file = f"{output_prefix}_{chunk_count}.csv"
            with open(output_file, 'w', newline='') as output:
                writer = csv.writer(output)
                writer.writerow(header)
                writer.writerows(records)

# Usage example
split_csv('input.csv', 'output', 950000)