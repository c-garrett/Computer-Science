from utils import *
from table_operations import *


class Dcube(object):
    def __init__(self):
        self.cur = open_cursor(conn)

    def find_single_block(self):
        return

    def select_dimension_cardinality(self):
        return

    def cubing(self):
        return


if __name__ == '__main__':
    conn = connect_database()
    cube = Dcube()
    close_database_connection(conn)
