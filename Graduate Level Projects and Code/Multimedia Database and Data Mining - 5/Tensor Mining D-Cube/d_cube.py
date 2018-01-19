from utils import *
from table_operations import *


class Dcube(object):
    def __init__(self):
        self.cur = open_cursor(conn)

    @staticmethod
    def find_single_block(mass_relation):
        # initialize the block B to R
        drop_table(conn, TABLE_NAME + "_block")
        copy_table(conn, TABLE_NAME, TABLE_NAME + "_block", "*")
        mass_block = mass_relation

        # Bn <- copy(Rn)
        rcs = "{}_cardinality_set".format(TABLE_NAME)
        bcs = "{}_block_cardinality_set".format(TABLE_NAME)
        drop_table(conn, rcs)
        drop_table(conn, bcs)
        cardinality_columns = ("attribute varchar({}), cardinality integer".format(COLUMN_LENGTH))
        create_empty_table_cols(conn, cardinality_columns, "_cardinality_set")
        create_empty_table_cols(conn, cardinality_columns, "_block_cardinality_set")

        block_set_cardinality_count = 0
        for dimension_attribute in COLS:
            relation_table = "{}_{}_set".format(TABLE_NAME, dimension_attribute)
            dimension_table = "{}_{}_block_set".format(TABLE_NAME, dimension_attribute)
            drop_table(conn, dimension_table)
            copy_table(conn, relation_table, dimension_table, "*")
            current_cardinality = aggregate_table_next_row(conn, "count({})".format(dimension_attribute), dimension_table)[0]
            block_set_cardinality_count += current_cardinality
            values = "'{}', {}".format(dimension_attribute, current_cardinality)
            insert_col(conn, rcs, values)
            insert_col(conn, bcs, values)



    def select_dimension_cardinality(self):
        return

    def cubing(self):
        # copy relation to relation_copy
        drop_table(conn, TABLE_NAME + "_copy")
        copy_table(conn, TABLE_NAME, TABLE_NAME + "_copy", "*")

        # compute the set of distinct attribute values within the relation and store in relation_distinct_set
        for dimension_attribute in COLS:
            dimension_table = "{}_{}_set".format(TABLE_NAME, dimension_attribute)
            drop_table(conn, dimension_table)
            copy_distinct_table(conn, TABLE_NAME, dimension_table, dimension_attribute)

        # create an empty relation results
        create_empty_table(conn, TABLE_NAME + "_results")

        for i in xrange(K):
            # mass calculation for the relation
            mass_relation = aggregate_table_next_row(conn, "count(*)", TABLE_NAME)[0]
            if mass_relation == 0: return

            # Algorithm-2 FIND SINGLE BLOCK
            self.find_single_block(mass_relation=mass_relation)

            return

        pass


if __name__ == '__main__':
    conn = connect_database()
    cube = Dcube()
    close_database_connection(conn)
