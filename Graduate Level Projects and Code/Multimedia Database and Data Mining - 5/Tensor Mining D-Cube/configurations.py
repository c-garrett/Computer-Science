# Database configurations
DATABASE_NAME = "postgres"
PORT = 5432
USERNAME = "postgres"
PASSWORD = ""
TABLE_NAME = "darpa_data"
COLS = ['source_ip', 'destination_ip', 'time_in_minutes']
COLUMN_LENGTH = 20

# Algorithm configurations
K = 3
DENSITY_MEASURE = "geometric_average"
DIMENSION_SELECTION_POLICY = "maximum_cardinality_first"

