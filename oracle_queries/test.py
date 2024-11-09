# Apply the imputer transformation separately
X_train_transformed = imputer.fit_transform(X_train)

# Ensure no columns are all zeros or removed; align column names properly
# Check the shape difference between transformed and original data
if X_train_transformed.shape[1] != X_train.shape[1]:
    print("Warning: The imputer has modified the number of columns.")

# Create the DataFrame using only valid columns from X_train
try:
    X_train_imputed = pd.DataFrame(X_train_transformed, columns=X_train.columns)
except ValueError:
    # If there's a mismatch, use the columns that match the transformed shape
    num_cols = X_train_transformed.shape[1]
    X_train_imputed = pd.DataFrame(X_train_transformed, columns=X_train.columns[:num_cols])

# Apply the same logic to X_test
X_test_transformed = imputer.transform(X_test)
try:
    X_test_imputed = pd.DataFrame(X_test_transformed, columns=X_train.columns[:num_cols])
except ValueError:
    # Adjust the column handling for consistency
    X_test_imputed = pd.DataFrame(X_test_transformed, columns=X_train.columns[:num_cols])

# write to file

# Convert predictions to a DataFrame
import pandas as pd

# Create a DataFrame with predictions
predictions_df = pd.DataFrame(yhat_test_xgb, columns=['Predicted'])

# Specify the output file path
output_file_path = 'yhat_test_predictions.csv'

# Save the DataFrame to a CSV file
predictions_df.to_csv(output_file_path, index=False)

print(f"Predictions have been saved to {output_file_path}")

