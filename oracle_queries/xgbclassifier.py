from xgboost import XGBClassifier
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
from sklearn.utils import class_weight
from imblearn.over_sampling import SMOTE
from sklearn.model_selection import GridSearchCV, train_test_split

# Modify the loop to handle class imbalance and include cross-validation
for fold_num in range(1, 2):
    print(f"Processing fold{fold_num}...")
    np.random.seed(1390)
    folder = f"fold{fold_num}"

    train_file_path = os.path.join(folder, 'train_small.csv')
    test_file_path = os.path.join(folder, 'test_small.csv')
    test_y_file_path = os.path.join(folder, 'test_y_small.csv')

    # Load the datasets
    housing_data_train = pd.read_csv(train_file_path)
    housing_data_test = pd.read_csv(test_file_path)
    test_y_data = pd.read_csv(test_y_file_path)

    # List of columns to one-hot encode
    columns_to_encode = ['feed_nm', 'column2', 'column3']

    # Apply one-hot encoding to the specified columns
    X_train = pd.get_dummies(X_train, columns=columns_to_encode, drop_first=True)
    X_test = pd.get_dummies(X_test, columns=columns_to_encode, drop_first=True)

    # Align columns to ensure consistent input features between training and testing
    X_train, X_test = X_train.align(X_test, join='left', axis=1)
    X_test.fillna(0, inplace=True)


    # Encode the target variable
    label_encoder = LabelEncoder()
    housing_data_train['MODIFIED'] = label_encoder.fit_transform(housing_data_train['MODIFIED'])
    test_y_data['MODIFIED'] = label_encoder.transform(test_y_data['MODIFIED'])

    # Remove specified features
    features_to_remove = ['Id', 'Latitude', 'Street', 'Utilities', 'Condition_2', 'LowQualFinSF', 'Pool_QC', 'MiscFeature', 'Misc_Val']
    housing_data_train = housing_data_train.drop(columns=features_to_remove)
    housing_data_test = housing_data_test.drop(columns=features_to_remove)

    # Prepare training and testing sets
    X_train = housing_data_train.drop('MODIFIED', axis=1)
    y_train = housing_data_train['MODIFIED']
    X_test = housing_data_test
    y_test = test_y_data['MODIFIED']

    # Handle class imbalance using SMOTE
    smote = SMOTE(random_state=42)
    X_train, y_train = smote.fit_resample(X_train, y_train)

    # One-hot encode categorical variables
    X_train = pd.get_dummies(X_train)
    X_test = pd.get_dummies(X_test)
    X_train, X_test = X_train.align(X_test, join='left', axis=1)
    X_test.fillna(0, inplace=True)

    # Initialize and train the XGBClassifier with adjusted parameters
    xgb_model = XGBClassifier(
        n_estimators=400,
        learning_rate=0.05,
        max_depth=8,
        subsample=0.8,
        colsample_bytree=0.6,
        reg_lambda=0.1,
        reg_alpha=0.01,
        gamma=0,
        scale_pos_weight=3,  # Adjust this based on the class imbalance ratio
        random_state=42
    )

    xgb_model.fit(X_train, y_train)

    # Predict on the training set
    yhat_train_xgb = xgb_model.predict(X_train)
    train_accuracy = accuracy_score(y_train, yhat_train_xgb)
    print(f"Fold{fold_num} XGBoost Train Accuracy: {train_accuracy}")

    # Predict on the test set
    yhat_test_xgb = xgb_model.predict(X_test)
    test_accuracy = accuracy_score(y_test, yhat_test_xgb)
    print(f"Fold{fold_num} XGBoost Test Accuracy: {test_accuracy}")

    # Display additional classification metrics
    print(classification_report(y_test, yhat_test_xgb))
