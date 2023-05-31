package ru.geekbrains;

import java.util.Random;
import java.util.Scanner;

public class TicTacToe {
    private static final int WIN_COUNT = 4;
    private static final char DOT_HUMAN = 'X';
    private static final char DOT_AI = 'O';
    private static final char DOT_EMPTY = '•';

    private static final Scanner SCANNER = new Scanner(System.in);

    private static final Random random = new Random();

    private static char[][] field;
    private static int fieldSizeX;
    private static int fieldSizeY;

    private static int[] coordinateDot = new int[2];


    public static void main(String[] args) {
        initialize();
        printField();
        while (true) {
            while (true) {
                coordinateDot = humanTurn();
                printField();
                if (gameCheck(coordinateDot, DOT_HUMAN, "Вы победили!")) {
                    break;
                }
                coordinateDot = aiTurn();
                printField();

                if (gameCheck(coordinateDot, DOT_AI, "Компьютер победил!")) {
                    break;
                }
            }
            System.out.println("Желаете сыграть еще раз? (Y - да)");

            if (!SCANNER.next().equalsIgnoreCase("Y")) {
                break;
            }
            initialize();
            printField();
        }

    }


    /**
     * Задаем размерность поля
     */
    private static void initialize() {
        fieldSizeX = 5;
        fieldSizeY = 5;

        field = new char[fieldSizeX][fieldSizeY];

        for (int x = 0; x < fieldSizeX; x++) {
            for (int y = 0; y < fieldSizeY; y++) {
                field[x][y] = DOT_EMPTY;
            }
        }
    }

    /**
     * Рисуем поле
     */
    private static void printField() {
        System.out.print("+");

        for (int i = 0; i < field.length * 2 + 1; i++) {
            System.out.print((i % 2 == 0) ? "-" : i / 2 + 1);
        }
        System.out.println();

        for (int i = 0; i < fieldSizeX; i++) {
            System.out.print(i + 1 + "|");

            for (int j = 0; j < fieldSizeY; j++) {
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }

        for (int i = 0; i < field.length * 2 + 2; i++) {
            System.out.print("-");
        }
        System.out.println("");
    }


    private static int[] humanTurn() {
        int x, y;
        do {
            System.out.print("Введите координаты хода X и Y (от 1 до 3) через пробел >>> ");
            x = SCANNER.nextInt() - 1;
            y = SCANNER.nextInt() - 1;
        }
        while (!isCellEmpty(x, y) || !isCellValid(x, y));
        field[x][y] = DOT_HUMAN;
        coordinateDot[0] = x;
        coordinateDot[1] = y;

        return coordinateDot;
    }

    private static int[] aiTurn() {
        int x, y;

        do {
            x = random.nextInt(fieldSizeX);
            y = random.nextInt(fieldSizeY);
        } while (!isCellEmpty(x, y));
        field[x][y] = DOT_AI;
        coordinateDot[0] = x;
        coordinateDot[1] = y;

        return coordinateDot;

    }

    /**
     * Метод проверки состояния игры
     */
    private static boolean gameCheck(int[] coordinate, char c, String str) {
        if (checkWin(coordinate, c)) {
            System.out.println(str);
            return true;
        }

        if (checkDraw()) {
            System.out.println("Ничья");
            return true;
        }

        return false;
    }

    static boolean checkWin(int[] coordinate, char dot) {
        int coordinateX = coordinate[0];
        int coordinateY = coordinate[1];

        return destination(coordinateX, coordinateY, dot);
    }

    /**
     * Определяет в каком направлении вызвать проверку на выигрышную комбинацию.
     * Добавила условия conditionX, conditionY, чтобы не дублировать циклы.
     */
    private static boolean destination(int x, int y, char dot) {
        boolean win;

        int coordinateX = 0;
        int coordinateY = 0;
        if (x > 0) {
            coordinateX = x - 1;
        }
        if (y > 0) {
            coordinateY = y - 1;
        }

        int conditionX; //условие для прохождения по X
        int conditionY; //условие для прохождения по Y
        if (x == fieldSizeX - 1) {
            conditionX = x;
        } else {
            conditionX = x + 1;
        }
        if (y == fieldSizeY - 1) {
            conditionY = y;
        } else {
            conditionY = y + 1;
        }


        for (int i = coordinateX; i <= conditionX; i++) {
            for (int j = coordinateY; j <= conditionY; j++) {
                if (i < x && j < y && field[i][j] == dot || i > x && j > y && field[i][j] == dot) {
                    win = diagonalLeft(i, j, dot);
                    if (win) {
                        return true;
                    }
                }
                if (i < x && j == y && field[i][j] == dot || i > x && j == y && field[i][j] == dot) {
                    win = vert(i, j, dot);
                    if (win) {
                        return true;
                    }
                }
                if (i < x && j > y && field[i][j] == dot || i > x && j < y && field[i][j] == dot) {
                    win = diagonalRight(i, j, dot);
                    if (win) {
                        return true;
                    }
                }
                if (i == x && j < y && field[i][j] == dot || i == x && j > y && field[i][j] == dot) {
                    win = horizontal(i, j, dot);
                    if (win) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Проверка по диагонали вправо вверх, если кол-во фишек не соответствует выигрышному,
     * то проверяет вниз влево от координат изначально переданной фишки
     *
     * @param x - координата рядом лежащей такой же фишки, что изначально была передана
     * @param y - координата рядом лежащей такой же фишки, что изначально была передана
     */
    private static boolean diagonalRight(int x, int y, char dot) {
        int count = 0;
        int coordinateY = y;

        for (int i = x; i <= fieldSizeX - 1; i++) {
            if (field[i][coordinateY] == dot) {
                count++;
                if (count == WIN_COUNT) {
                    return true;
                }
                if (coordinateY != 0) {
                    coordinateY--;
                } else {
                    break;
                }
            }

        }

        coordinateY = y;
        if (count < WIN_COUNT && x != 0) {
            for (int k = x - 1; k >= 0; k--) {
                if (coordinateY != fieldSizeY - 1)
                    coordinateY += 1;
                if (field[k][coordinateY] == dot) {
                    count++;
                } else {
                    return false;
                }
                if (count == WIN_COUNT) {
                    return true;
                }
            }

        }

        return false;
    }

    /**
     * Проверка по диагонали вниз вправо, если кол-во фишек не соответствует выигрышному, то проверяет вверх влево
     * от координат изначально переданной фишки
     *
     * @param x   - координата рядом лежащей такой же фишки, что изначально была передана
     * @param y   - координата рядом лежащей такой же фишки, что изначально была передана
     */
    private static boolean diagonalLeft(int x, int y, char dot) {
        int count = 0;
        int coordinateY = y;

        for (int i = x; i <= fieldSizeX - 1; i++) {
            if (field[i][coordinateY] == dot) {
                count++;
                if (count == WIN_COUNT) {
                    return true;
                }
                if (coordinateY != fieldSizeY - 1) {
                    coordinateY++;
                } else {
                    break;
                }
            }

        }

        coordinateY = y;
        if (count < WIN_COUNT && x != 0) {
            for (int k = x - 1; k >= 0; k--) {
                if (coordinateY != 0)
                    coordinateY -= 1;
                if (field[k][coordinateY] == dot) {
                    count++;
                } else {
                    return false;
                }
                if (count == WIN_COUNT) {
                    return true;
                }
            }

        }

        return false;
    }

    /**
     * Проверка вправо по горизонтали, если кол-во фишек не соответствует выигрышному,
     * то проверяет влево от координат изначально переданной фишки
     *
     * @param x   - координата рядом лежащей такой же фишки, что изначально была передана
     * @param y   - координата рядом лежащей такой же фишки, что изначально была передана
     */
    private static boolean horizontal(int x, int y, char dot) {
        int count = 0;

        for (int i = x; i < x + 1; i++) {
            for (int j = y; j <= fieldSizeY - 1; j++) {
                if (field[i][j] == dot) {
                    count++;
                } else {
                    break;
                }
                if (count == WIN_COUNT) {
                    return true;
                }
            }
        }

        if (count < WIN_COUNT && y != 0) {
            for (int i = x; i < x + 1; i++) {
                for (int j = y - 1; j >= 0; j--) {
                    if (field[i][j] == dot) {
                        count++;
                    } else {
                        return false;
                    }
                    if (count == WIN_COUNT) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Проверка вниз по вертикали, если кол-во фишек не соответствует выигрышному,
     * то проверяет вверх от координат изначально переданной фишки
     *
     * @param x   - координата рядом лежащей такой же фишки, что изначально была передана
     * @param y   - координата рядом лежащей такой же фишки, что изначально была передана
     */
    private static boolean vert(int x, int y, char dot) {
        int count = 0;

        for (int i = x; i <= fieldSizeX - 1; i++) {
            for (int j = y; j < y + 1; j++) {
                if (field[i][j] == dot) {
                    count++;
                } else {
                    break;
                }
                if (count == WIN_COUNT) {
                    return true;
                }
            }
        }

        if (count < WIN_COUNT && x != 0) {
            for (int i = x - 1; i >= 0; i--) {
                for (int j = y; j < y + 1; j++) {
                    if (field[i][j] == dot) {
                        count++;
                    } else {
                        return false;
                    }
                    if (count == WIN_COUNT) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean checkDraw() {
        for (int x = 0; x < fieldSizeX; x++) {
            for (int y = 0; y < fieldSizeY; y++) {
                if (isCellEmpty(x, y))
                    return false;
            }
        }
        return true;
    }


    static boolean isCellValid(int x, int y) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }

    static boolean isCellEmpty(int x, int y) {
        return field[x][y] == DOT_EMPTY;
    }
}