"""
Long Short Term Memory for character level entity classification
"""
import argparse, math, time, copy
from utils import *
from autograd import *

np.random.seed(0)

import pprint as pp


class LSTM(object):
    """
    Long Short Term Memory + Feedforward layer
    Accepts maximum length of sequence, input size, number of hidden units and output size
    """

    def __init__(self, max_len, in_size, num_hid, out_size, nsample=1):
        """
        Constructor: initialize LSTM model.
        """
        self.my_xman = self._build(max_len, in_size, num_hid, out_size, nsample)

    def _build(self, max_len, in_size, num_hid, out_size, nsample):
        """
        Define the model of LSTM, and initialize all values.
        """
        xm = XMan()
        for i in xrange(1, max_len + 1):
            setattr(xm, "x{}".format(i), f.input(name="x{}".format(i), default=np.random.rand(nsample, in_size)))
        y = np.zeros((nsample, out_size))
        for i in xrange(nsample): y[i, np.random.choice(out_size)] = 1
        xm.y = f.input(name="y", default=y)

        bound_dict = dict()
        bound_dict["W"] = math.sqrt(6.0 / (in_size + num_hid))
        bound_dict["b"] = .1
        bound_dict["U"] = math.sqrt(6.0 / (num_hid + num_hid))
        bound_dict["W2"] = math.sqrt(6.0 / (num_hid + out_size))

        """
        First layer LSTM
        """

        for char in ["f", "i", "c", "o"]:
            setattr(xm, "W{}".format(char), f.param(name="W{}".format(char),
                                                    default=np.random.uniform(low=-bound_dict["W"],
                                                                              high=bound_dict["W"],
                                                                              size=(in_size, num_hid))))
            setattr(xm, "U{}".format(char), f.param(name="U{}".format(char),
                                                    default=np.random.uniform(low=-bound_dict["U"],
                                                                              high=bound_dict["U"],
                                                                              size=(num_hid, num_hid))))
            setattr(xm, "b{}".format(char), f.param(name="b{}".format(char),
                                                    default=np.random.uniform(low=-bound_dict["b"],
                                                                              high=bound_dict["b"],
                                                                              size=num_hid)))

        xm.c0 = f.input(name="c0", default=np.zeros((nsample, num_hid)))
        xm.h0 = f.input(name="h0", default=np.zeros((nsample, num_hid)))

        for t in xrange(1, max_len + 1):
            setattr(xm, "i{}".format(t),
                    f.sigmoid(
                        f.matrix_mul(getattr(xm, "x{}".format(t)), xm.Wi) +
                        f.matrix_mul(getattr(xm, "h{}".format(t - 1)), xm.Ui)
                        + xm.bi)
                    )
            setattr(xm, "f{}".format(t),
                    f.sigmoid(
                        f.matrix_mul(getattr(xm, "x{}".format(t)), xm.Wf) +
                        f.matrix_mul(getattr(xm, "h{}".format(t - 1)), xm.Uf)
                        + xm.bf)
                    )
            setattr(xm, "o{}".format(t),
                    f.sigmoid(
                        f.matrix_mul(getattr(xm, "x{}".format(t)), xm.Wo) +
                        f.matrix_mul(getattr(xm, "h{}".format(t - 1)), xm.Uo)
                        + xm.bo)
                    )
            setattr(xm, "c_tilde{}".format(t),
                    f.tanh(
                        f.matrix_mul(getattr(xm, "x{}".format(t)), xm.Wc) +
                        f.matrix_mul(getattr(xm, "h{}".format(t - 1)), xm.Uc)
                        + xm.bc)
                    )

            setattr(xm, "c{}".format(t),
                    f.elem_mul(getattr(xm, "f{}".format(t)), getattr(xm, "c{}".format(t - 1)))
                    + f.elem_mul(getattr(xm, "i{}".format(t)), getattr(xm, "c_tilde{}".format(t)))
                    )
            setattr(xm, "h{}".format(t),
                    f.elem_mul(getattr(xm, "o{}".format(t)),
                               f.tanh(getattr(xm, "c{}".format(t))))
                    )

        """
            Second Layer
        """

        xm.W2 = f.param(name="W2",
                        default=np.random.uniform(low=-bound_dict["W2"],
                                                  high=bound_dict["W2"],
                                                  size=(num_hid, out_size)))
        xm.b2 = f.param(name="b2",
                        default=np.random.uniform(low=-bound_dict["b"],
                                                  high=bound_dict["b"],
                                                  size=out_size))

        xm.o = f.relu(f.matrix_mul(getattr(xm, "h{}".format(t)), xm.W2) + xm.b2)
        xm.p = f.softMax(xm.o)
        xm.loss = f.crossEnt(xm.p, xm.y)

        return xm.setup()


def main(params):
    epochs = params['epochs']
    max_len = params['max_len']
    num_hid = params['num_hid']
    batch_size = params['batch_size']
    dataset = params['dataset']
    init_lr = params['init_lr']
    output_file = params['output_file']
    epsilon = params['epsilon']

    # load data and preprocess
    dp = DataPreprocessor()
    data = dp.preprocess('%s.train' % dataset, '%s.valid' % dataset, '%s.test' % dataset)
    mb_train = MinibatchLoader(data.training, batch_size, max_len,
                               len(data.chardict), len(data.labeldict))
    mb_valid = MinibatchLoader(data.validation, len(data.validation), max_len,
                               len(data.chardict), len(data.labeldict), shuffle=False)
    mb_test = MinibatchLoader(data.test, len(data.test), max_len,
                              len(data.chardict), len(data.labeldict), shuffle=False)
    # build
    print "building lstm..."
    lstm = LSTM(max_len, mb_train.num_chars, num_hid, mb_train.num_labels)

    # check gradient
    print "Checking gradients..."
    my_xman = lstm.my_xman
    ad = Autograd(my_xman)
    wengert_list = my_xman.operationSequence(my_xman.loss)
    value_dict = ad.eval(wengert_list, my_xman.inputDict())
    gradients = ad.bprop(wengert_list, value_dict, loss=np.float_(1.))

    for key in value_dict:
        if my_xman.isParam(key):
            # Check the gradients of each parameter
            for index, val in np.ndenumerate(value_dict[key]):
                value_dict[key][index] = val + epsilon
                j_positive = ad.eval(wengert_list, value_dict)["loss"]
                value_dict[key][index] = val - epsilon
                j_negative = ad.eval(wengert_list, value_dict)["loss"]
                grad_approx = (j_positive - j_negative) / (2 * epsilon)
                if abs(grad_approx - gradients[key][index]) > 1e-4:
                    print("Gradient Checking failed for: {}".format(key))
                    exit(0)
                value_dict[key][index] = val

    # train
    print "training..."
    my_xman = lstm.my_xman
    value_dict = my_xman.inputDict()
    ad = Autograd(my_xman)
    wengert_list = my_xman.operationSequence(my_xman.loss)

    lr = init_lr
    min_val_loss = float('Inf')
    min_validation_dict = {}
    train_times = []

    for i in range(epochs):
        mb_train.reset()
        mb_valid.reset()
        start_time = time.time()

        # mini-batch sgd
        for (idxs, e, l) in mb_train:
            n = len(idxs)
            for i in xrange(1, max_len + 1):
                value_dict["x{}".format(i)] = e[:, max_len - i, :]
            value_dict['y'] = l
            value_dict['c0'] = np.zeros((n, num_hid))
            value_dict['h0'] = np.zeros((n, num_hid))

            # feed-forward
            value_dict = ad.eval(wengert_list, value_dict)
            # back-propagation
            gradients = ad.bprop(wengert_list, value_dict, loss=np.float_(1.))

            for key in gradients:
                if my_xman.isParam(key):
                    value_dict[key] -= lr * gradients[key]

        train_times.append(time.time() - start_time)

        # validation loss
        (idxs, e, l) = mb_valid.next()
        n = len(idxs)
        for i in xrange(1, max_len + 1):
            value_dict["x{}".format(i)] = e[:, max_len - i, :]
        value_dict['y'] = l
        value_dict['c0'] = np.zeros((n, num_hid))
        value_dict['h0'] = np.zeros((n, num_hid))
        value_dict = ad.eval(wengert_list, value_dict)
        validation_loss = value_dict["loss"]

        if validation_loss < min_val_loss:
            min_validation_dict = copy.deepcopy(value_dict)
            min_val_loss = validation_loss

    print("Total training time {}".format(np.sum(train_times)))

    # Test
    test_loss = []
    for (idxs, e, l) in mb_test:
        n = len(idxs)
        for i in xrange(1, max_len + 1):
            min_validation_dict["x{}".format(i)] = e[:, max_len - i, :]
        min_validation_dict['y'] = l
        min_validation_dict['c0'] = np.zeros((n, num_hid))
        min_validation_dict['h0'] = np.zeros((n, num_hid))
        min_validation_dict = ad.eval(wengert_list, value_dict)
        test_loss.append(min_validation_dict["loss"])

    test_loss_avg = np.sum(test_loss) / len(test_loss)

    print("Best validation loss:{}".format(min_val_loss))
    print("Test loss (avg mini-batches):{}".format(test_loss_avg))
    print("Test loss per batch:{}".format(test_loss))


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--max_len', dest='max_len', type=int, default=10)
    parser.add_argument('--num_hid', dest='num_hid', type=int, default=50)
    parser.add_argument('--batch_size', dest='batch_size', type=int, default=64)
    parser.add_argument('--dataset', dest='dataset', type=str, default='tiny')
    parser.add_argument('--epochs', dest='epochs', type=int, default=15)
    parser.add_argument('--init_lr', dest='init_lr', type=float, default=0.5)
    parser.add_argument('--output_file', dest='output_file', type=str, default='output')
    parser.add_argument('--epsilon', dest='epsilon', type=float, default=1e-4)
    params = vars(parser.parse_args())
    main(params)
