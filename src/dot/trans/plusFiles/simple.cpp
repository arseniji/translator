int sum(int a, int b) {
    int result = a + b;
    return result;
}

int main() {
    int x = 5;
    int y;
    int arr[3];
    y = 7;
    arr[0] = x;
    arr[1] = y;
    arr[2] = sum(x, y);

    if (x > 10) {
        cout << "Greater" << endl;
    } else {
        cout << "Smaller or equal" << endl;
    }
    int i = 0;
    while (i < 3) {
        cout << arr[i] << endl;
        i++;
    }

    do {
        x--;
    } while (x > 0);

    for (int j = 0; j < 5; j++) {
        if (j == 2) j++;
        if (j == 4) j++;
        cout << j << endl;
    }

    return 0;
}