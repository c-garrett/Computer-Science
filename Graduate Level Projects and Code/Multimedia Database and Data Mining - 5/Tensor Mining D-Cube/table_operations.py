from utils import *
from configurations import *


def drop_table(conn, target):
    cur = open_cursor(conn)
    cur.execute("DROP TABLE IF EXISTS {}_{}".format(TABLE_NAME, target))
    conn.commit()
    close_cursor(cur)


def copy_table(conn, source, target, cols):
    cur = open_cursor(conn)
    cur.execute("SELECT {} INTO {} FROM {}".format(cols, target, source))
    conn.commit()
    close_cursor(cur)


def create_empty_table(conn, target):
    cur = open_cursor(conn)
    cur.execute("CREATE TABLE {} (source_ip VARCHAR(50), destination_ip VARCHAR(50) time_in_mintues VARCHAR(50)".format(target))
    conn.commit()
    close_cursor(cur)

def create_empty_table_cols(conn, cols, target):
    cur = open_cursor(conn)
    cur.execute("CREATE TABLE {} ({})".format(target, cols))
    conn.commit()
    close_cursor(cur)

def add_col(conn, target, column):
    cur = open_cursor(conn)
    cur.execute("ALTER TABLE {} ADD COLUMN {}".format(target, column))
    conn.commit()
    close_cursor(cur)


def copy_distinct_table(conn, source, target, col):
    cur = open_cursor(conn)
    cur.execute("CREATE TABLE {} AS SELECT DISTINCT {} FROM {}".format(source, target, col))
    conn.commit()
    close_cursor(cur)


def aggregate_table_next_row(conn, aggregate, table):
    cur = open_cursor(conn)
    cur.execute("SELECT {} FROM {}".format(aggregate, table))
    row = cur.fetchone()
    conn.commit()
    close_cursor(cur)
    return row

def insert_col(conn, target, value):
    cur = open_cursor(conn)
    cur.execute("INSERT INTO {} VALUES({})".format(target, value))
    conn.commit()
    close_cursor(cur)
