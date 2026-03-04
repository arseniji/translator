int i = 0;
int j = 0;
while (i < 5) {
    j = 0;
    while (j < 5) {
        if (j == 3) break;
        j = j + 1;
    }
    if (i == 2) continue;
    i = i + 1;
}

int sum = 0;
for (int i = 0; i < 10; i++) {
    if (i % 2 == 0) {
        sum = sum + i;
    } else {
        sum = sum - 1;
    }
}

int x = 5;
if (x == 1) {
    x = 10;
} else if (x == 2) {
    x = 20;
} else if (x == 3) {
    x = 30;
} else if (x == 4) {
    x = 40;
} else {
    x = 99;
}

int n = 1;
do {
    n = n * 2;
    if (n == 8) break;
} while (n < 100);

int a = 3;
int b = 5;
if (a > 0)
    if (b > 0)
        a = a + b;
    else
        a = 0;
else
    b = 0;

    int max(int a, int b) {
        if (a > b) return a;
        else return b;
    }

    int main() {
        int x = 10;
        int y = 20;
        int result = max(x, y);
        cout << result;
        return 0;
    }

    int main() {
        int arr[5];
        for (int i = 0; i < 5; i++) {
            arr[i] = i * 2;
        }
        cout << arr[3];
        return 0;
    }

    int a = 2;
    int b = 3;
    int c = 4;
    int result = a + b * c - (a + b) * c / 2;
    bool flag = a > 0 && b < 10 || c == 4;

    int main() {
        int a;
        int b;
        cin >> a >> b;
        int sum = a + b;
        cout << "Sum: " << sum << endl;
        return 0;
    }

    int factorial(int n) {
        int result = 1;
        for (int i = 2; i <= n; i++) {
            result = result * i;
        }
        return result;
    }

    int main() {
        int n;
        cin >> n;
        if (n < 0) {
            cout << "Error" << endl;
        } else if (n == 0) {
            cout << 1 << endl;
        } else {
            int f = factorial(n);
            cout << f << endl;
        }
        return 0;
    }