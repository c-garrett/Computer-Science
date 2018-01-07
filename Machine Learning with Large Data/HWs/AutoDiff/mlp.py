"""
Multilayer Perceptron for character level entity classification
"""
import argparse, math, time, copy
from utils import *
from autograd import *

np.random.seed(0)


class MLP(object):
    """
    Multilayer Perceptron
    Accepts list of layer sizes [in_size, hid_size1, hid_size2, ..., out_size]
    """

    def __init__(self, layer_sizes):
        self.my_xman = self._build(layer_sizes)

    def _build(self, layer_sizes, n=1):  # n is used for debugging gradients. Overwritten when getting True inputs
        """
        Model definition and variables
        """
        xm = XMan()
        xm.o0 = f.input(name="x", default=np.random.rand(n, layer_sizes[0]))
        y = np.zeros((n, layer_sizes[-1]))
        y[0, np.random.choice(layer_sizes[-1])] = 1
        xm.y = f.input(name="y", default=y)

        for i in range(len(layer_sizes) - 1):
            bound = math.sqrt(6.0 / (layer_sizes[i] + layer_sizes[i + 1]))
            setattr(xm, "W{}".format(i),
                    f.param(name="W{}".format(i), default=np.random.uniform(low=-bound, high=bound,
                                                                            size=(layer_sizes[i], layer_sizes[i + 1]))))
            setattr(xm, "b{}".format(i),
                    f.param(name="b{}".format(i), default=np.random.uniform(low=-.1, high=.1,
                                                                            size=(layer_sizes[i + 1]))))
            setattr(xm, "o{}".format(i + 1),
                    f.relu(
                        f.matrix_mul(getattr(xm, "o{}".format(i)),
                                     getattr(xm, "W{}".format(i))) + getattr(xm, "b{}".format(i))))

        xm.output = f.softMax(getattr(xm, "o{}".format(i + 1)))
        xm.loss = f.crossEnt(xm.output, xm.y)
        return xm.setup()


def main(params):
    # parameters
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

    # minibatches
    mb_train = MinibatchLoader(data.training, batch_size, max_len,
                               len(data.chardict), len(data.labeldict))
    mb_valid = MinibatchLoader(data.validation, len(data.validation), max_len,
                               len(data.chardict), len(data.labeldict), shuffle=False)
    mb_test = MinibatchLoader(data.test, len(data.test), max_len,
                              len(data.chardict), len(data.labeldict), shuffle=False)

    num_chars = mb_train.num_chars

    # build
    print("building mlp...")
    mlp = MLP([max_len * mb_train.num_chars, num_hid, mb_train.num_labels])

    # Check gradient
    print "Checking gradients..."
    my_xman = mlp.my_xman
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
                    break
                value_dict[key][index] = val

    # train
    print("training...")
    my_xman = mlp.my_xman
    value_dict = my_xman.inputDict()
    ad = Autograd(my_xman)
    wengert_list = my_xman.operationSequence(my_xman.loss)

    print("value_dict:{}".format(value_dict.keys()))
    print("Wengert List:{}".format(wengert_list))

    lr = init_lr
    min_val_loss = float('Inf')
    min_validation_dict = {}
    train_times = []

    for i in range(epochs):
        mb_train.reset()
        mb_valid.reset()
        start_time = time.time()

        # mini-batch SGD
        for (idxs, e, l) in mb_train:
            cur_batch_size = len(idxs)
            value_dict["x"] = e.reshape(cur_batch_size, max_len * num_chars)
            value_dict["y"] = l

            # feed-forward
            value_dict = ad.eval(wengert_list, value_dict)
            # back-propagation
            gradients = ad.bprop(wengert_list, value_dict, loss=np.float_(1.))

            # update parameters
            for rname in gradients:
                if mlp.my_xman.isParam(rname):
                    value_dict[rname] -= lr * gradients[rname]

        train_times.append(time.time() - start_time)

        # validation loss
        (idxs, e, l) = mb_valid.next()
        value_dict["x"] = e.reshape(len(idxs), max_len * num_chars)
        value_dict["y"] = l
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
        min_validation_dict["x"] = e.reshape(n, max_len * num_chars)
        min_validation_dict["y"] = l
        min_validation_dict = ad.eval(wengert_list, min_validation_dict)
        test_loss.append(min_validation_dict["loss"])

    test_loss_avg = np.sum(test_loss) / len(test_loss)

    print("Best validation loss:{}".format(min_val_loss))
    print("Test loss (avg mini-batches):{}".format(test_loss_avg))
    print("Test loss per batch:{}".format(test_loss))

    return


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
