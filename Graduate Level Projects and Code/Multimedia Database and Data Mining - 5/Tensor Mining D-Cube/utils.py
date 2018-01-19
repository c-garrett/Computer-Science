import psycopg2 as psycopg2
from configurations import *


def connect_database():
    conn = psycopg2.connect(dbname=DATABASE_NAME, user=USERNAME, password=PASSWORD, port=PORT)
    return conn


def close_database_connection(conn):
    conn.close()


def open_cursor(conn):
    return conn.cursor()


def close_cursor(cur):
    cur.close()
