#include <iostream>
#include <cmath>
using namespace std;

int main() {
    int number;
    bool isPrime = true;

    cout << "Введите целое число: ";
    cin >> number;

    // Проверка на чётность
    if (number % 2 == 0) {
        cout << "Число " << number << " является чётным." << endl;
    } else {
        cout << "Число " << number << " является нечётным." << endl;
    }

    // Проверка на простоту (для чисел > 1)
    if (number > 1) {
        for (int i = 2; i <= sqrt(number); i++) {
            if (number % i == 0) {
                isPrime = false;
                break;
            }
        }
        if (isPrime) {
            cout << "Число " << number << " является простым." << endl;
        } else {
            cout << "Число " << number << " является составным." << endl;
        }
    } else if (number == 1) {
        cout << "Число 1 не является ни простым, ни составным." << endl;
    } else {
        cout << "Число отрицательное или нуль." << endl;
    }

    return 0;
}