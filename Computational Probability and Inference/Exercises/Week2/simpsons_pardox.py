from Exercises import comp_prob_inference
from Exercises.Week2.simpsons_pardox_data import*
import numpy as np


if __name__ == '__main__':
    print(joint_prob_table)
    print(joint_prob_table[gender_mapping['female'], department_mapping['C'], admission_mapping['admitted']])
    joint_prob_gender_admission = joint_prob_table.sum(axis=1)
    print(joint_prob_gender_admission)
    print(joint_prob_gender_admission[gender_mapping['female'], admission_mapping['admitted']])

    # conditioning on female
    female_only = joint_prob_gender_admission[gender_mapping['female']]
    print(female_only)
    prob_admission_given_female = female_only / np.sum(female_only)
    print(prob_admission_given_female)
    prob_admission_given_female_dict = dict(zip(admission_labels, prob_admission_given_female))
    print(prob_admission_given_female_dict)

    # conditioning on admitted
    admitted_only = joint_prob_gender_admission[:, admission_mapping['admitted']]
    prob_gender_given_admitted = admitted_only / np.sum(admitted_only)
    prob_gender_given_admitted_dict = dict(zip(gender_labels, prob_gender_given_admitted))
    print(prob_gender_given_admitted_dict)

    # admitted gender and department
    for department in department_labels:
        for gender in gender_labels:
            restricted = joint_prob_table[gender_mapping[gender], department_mapping[department]]
            print(department, gender, dict(zip(admission_labels, restricted / np.sum(restricted)))['admitted'])

