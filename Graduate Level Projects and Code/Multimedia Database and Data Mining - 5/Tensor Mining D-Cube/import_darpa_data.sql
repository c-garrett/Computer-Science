DROP TABLE darpa_data;

CREATE TABLE darpa_data (
  source_ip VARCHAR(50),
  destination_ip VARCHAR(50),
  time_in_mintues VARCHAR(50)
);

COPY darpa_data FROM 'C:\Users\Owner\Desktop\TensorMining-DCUBE\darpa.csv' WITH (FORMAT csv);