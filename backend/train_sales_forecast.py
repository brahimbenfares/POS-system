import pandas as pd
import numpy as np
from sqlalchemy import create_engine
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
import joblib
import configparser
import skl2onnx
from skl2onnx import convert_sklearn
from skl2onnx.common.data_types import FloatTensorType

# Load database config from dbconfig.properties
def load_db_config():
    config = configparser.ConfigParser()
    config.read("walid-backend/src/main/resources/dbconfig.properties")
    return {
        "host": config.get("DEFAULT", "dbUrl").split("//")[1].split(":")[0],  # Extract host from dbUrl
        "user": config.get("DEFAULT", "dbUsername"),
        "password": config.get("DEFAULT", "dbPassword"),
        "database": config.get("DEFAULT", "dbUrl").split("/")[-1]  # Extract DB name
    }

# Fetch sales data using SQLAlchemy
#def fetch_sales_data():
    #db_config = load_db_config()

    # Create SQLAlchemy engine
    #engine = create_engine(f"mysql+mysqlconnector://{db_config['user']}:{db_config['password']}@{db_config['host']}/{db_config['database']}")

    #query = """
   # SELECT o.order_date, oi.product_id, SUM(oi.quantity) AS total_sold
   # FROM orders o
    #JOIN order_items oi ON o.order_id = oi.order_id
    #WHERE o.action = 'Confirm'
    #GROUP BY o.order_date, oi.product_id
    #ORDER BY o.order_date ASC
    #"""
    
    # Fetch data using Pandas and SQLAlchemy
    #df = pd.read_sql(query, engine)
    #return df
def fetch_sales_data():
    db_config = load_db_config()

    # Create SQLAlchemy engine
    engine = create_engine(f"mysql+mysqlconnector://{db_config['user']}:{db_config['password']}@{db_config['host']}/{db_config['database']}")

    query = """
    SELECT o.order_date, oi.product_id, 
           SUM(oi.quantity) AS total_sold,
           AVG(SUM(oi.quantity)) OVER (PARTITION BY oi.product_id ORDER BY o.order_date ROWS BETWEEN 6 PRECEDING AND CURRENT ROW) AS moving_avg
    FROM orders o
    JOIN order_items oi ON o.order_id = oi.order_id
    WHERE o.action = 'Confirm'
    GROUP BY o.order_date, oi.product_id
    ORDER BY o.order_date ASC
    """

    df = pd.read_sql(query, engine)
    return df

# Load sales data
#df = fetch_sales_data()
#df['order_date'] = pd.to_datetime(df['order_date'])
#df['day_number'] = (df['order_date'] - df['order_date'].min()).dt.days

# Prepare training data
#X = df[['day_number', 'product_id']]
#y = df['total_sold']
#X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Train AI model
#model = LinearRegression()
#model.fit(X_train, y_train)

# Convert model to ONNX format
#onnx_model = convert_sklearn(model, initial_types=[("input", FloatTensorType([None, 2]))])
#onnx_model_path = "sales_forecast_model.onnx"

# Save model
#with open(onnx_model_path, "wb") as f:
 #   f.write(onnx_model.SerializeToString())

#print("✅ AI Model trained and saved as 'sales_forecast_model.onnx'")

# Load sales data
df = fetch_sales_data()
df['order_date'] = pd.to_datetime(df['order_date'])
df['day_number'] = (df['order_date'] - df['order_date'].min()).dt.days

# Fill NaN values in moving_avg
df['moving_avg'].fillna(df['total_sold'], inplace=True)

# Prepare training data
X = df[['day_number', 'product_id', 'moving_avg']]
y = df['total_sold']

# Train/test split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Train AI model
model = LinearRegression()
model.fit(X_train, y_train)

# Convert model to ONNX format
onnx_model = convert_sklearn(model, initial_types=[("input", FloatTensorType([None, 3]))])
onnx_model_path = "sales_forecast_model.onnx"

# Save model
with open(onnx_model_path, "wb") as f:
    f.write(onnx_model.SerializeToString())

print("✅ AI Model trained and saved as 'sales_forecast_model.onnx'")

