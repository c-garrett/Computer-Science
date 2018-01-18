from utils import *
from postgres_config import *


def drop_table(conn, target):
    cur = open_cursor(conn)
    cur.execute("DROP TABLE IF EXISTS {}_{}".format(TABLE_NAME, target))
    close_cursor(cur)


def copy_table(conn, source, target):
    cur = open_cursor(conn)
    cur.execute("SELECT * INTO {} FROM {}".format(target, source))
    close_cursor(cur)

def create_empty_table(conn, target):
    return