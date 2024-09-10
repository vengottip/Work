import pandas as pd
import re
from datetime import datetime, timedelta

# Function to get all month-end dates in the last 20 years
def get_monthend_dates():
    today = datetime.today()
    end_dates = []
    for year in range(today.year - 20, today.year + 1):
        for month in range(1, 13):
            # Last day of the month
            if month == 12:
                next_month = datetime(year + 1, 1, 1)
            else:
                next_month = datetime(year, month + 1, 1)
            last_day = next_month - timedelta(days=1)
            end_dates.append(last_day.strftime('%Y-%m-%d'))
    return end_dates

# Function to check if the file name contains 'archive' and valid date, or 'MONTHLY'
def check_conditions(row, monthend_dates):
    file_name = row['file_name']
    file_name_lower = file_name.lower()
    
    # Check for 'archive'
    if 'archive' in file_name_lower:
        # Extract date in format yyyy-mm-dd
        match = re.search(r'\d{4}-\d{2}-\d{2}', file_name)
        if match:
            date = match.group(0)
            if date in monthend_dates:
                return True

    # Check for 'MONTHLY' (case-insensitive)
    if 'monthly' in file_name_lower:
        return True

    return False

# Read the CSV file
df = pd.read_csv('your_file.csv')

# Get the month-end dates for the last 20 years
monthend_dates = get_monthend_dates()

# Filter rows based on conditions
filtered_df = df[df.apply(check_conditions, axis=1, monthend_dates=monthend_dates)]

# Write the filtered rows to a new CSV file
filtered_df.to_csv('monthend_records.csv', index=False)

print("Filtered records have been written to monthend_records.csv")
