package ru.mpei;

public class Container<T> {

    private T[] array; // Массив для хранения элементов контейнера
    protected Container <T> next; // Ссылка на следующий контейнер
    private Container <T> prev; // Ссылка на предыдущий контейнер
    private int firstElement; // Индекс первого элемента в контейнере
    private int lastElement; // Индекс последнего элемента в контейнере

    // Конструктор, инициализирующий контейнер с заданной вместимостью
    public Container(int containerCapacity) {
        this.array = (T[]) new Object[containerCapacity]; // Создаем массив заданной вместимости
        this.next = null; // Изначально ссылка на следующий контейнер равна null
        this.prev = null; // Изначально ссылка на предыдущий контейнер равна null
        this.firstElement = 0; // Устанавливаем индекс первого элемента в 0
        this.lastElement = 0; // Устанавливаем индекс последнего элемента в 0
    }

    // Метод для получения массива элементов контейнера
    public T[] getArray() {
        return array; // Возвращаем массив
    }

    // Метод для установки элемента в заданном индексе массива
    public void setArray(int index, T element) {
        array[index] = element; // Устанавливаем элемент по указанному индексу
    }

    // Метод для получения следующего контейнера
    public Container<T> getNext() {
        return next; // Возвращаем ссылку на следующий контейнер
    }

    // Метод для установки следующего контейнера
    public void setNext(Container<T> next) {
        this.next = next; // Устанавливаем ссылку на следующий контейнер
    }

    // Метод для получения предыдущего контейнера
    public Container<T> getPrev() {
        return prev; // Возвращаем ссылку на предыдущий контейнер
    }

    // Метод для установки предыдущего контейнера
    public void setPrev(Container<T> prev) {
        this.prev = prev; // Устанавливаем ссылку на предыдущий контейнер
    }

    // Метод для получения индекса первого элемента
    public int getFirstElement() {
        return firstElement; // Возвращаем индекс первого элемента
    }

    // Метод для установки индекса первого элемента
    public void setFirstElement(int firstElement) {
        this.firstElement = firstElement; // Устанавливаем индекс первого элемента
    }

    // Метод для получения индекса последнего элемента
    public int getLastElement() {
        return lastElement; // Возвращаем индекс последнего элемента
    }

    // Метод для установки индекса последнего элемента
    public void setLastElement(int lastElement) {
        this.lastElement = lastElement; // Устанавливаем индекс последнего элемента
    }
}
