package ru.mpei;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NewIterator<T> implements Iterator<T> {

    private Container<T> iterContainer; // Текущий контейнер, по которому мы итерируемся
    private int idEl; // Индекс текущего элемента в контейнере
    private T element; // Хранит текущий элемент при итерации
    private int contCapacity; // Вместимость контейнера

    // Конструктор, инициализирующий итератор с заданным контейнером и его вместимостью
    public NewIterator(Container<T> iterContainer, int contCapacity)  {
        this.iterContainer = iterContainer; // Устанавливаем текущий контейнер
        this.idEl = iterContainer.getFirstElement(); // Инициализируем индекс первого элемента
        this.contCapacity = contCapacity; // Устанавливаем емкость контейнера
    }

    @Override
    // Метод для проверки, есть ли следующий элемент в итераторе
    public boolean hasNext() {
        // Проверяем, не достигли ли конца контейнера и не является ли текущий элемент null
        if (idEl < contCapacity && iterContainer.getArray()[idEl] != null) {
            return true; // Если есть следующий элемент, возвращаем true
        } else if (iterContainer.getNext() != null) {
            // Если текущий контейнер исчерпан, проверяем следующий контейнер
            iterContainer = iterContainer.getNext(); // Переходим к следующему контейнеру
            idEl = 0; // Сбрасываем индекс к первому элементу нового контейнера
            return hasNext(); // Рекурсивно проверяем следующий контейнер
        }
        return false; // Если нет следующего элемента, возвращаем false
    }

    @Override
    // Метод для получения следующего элемента итератора
    public T next() {
        // Проверяем, есть ли следующий элемент
        if (hasNext()){
            this.element = (T) this.iterContainer.getArray()[this.idEl]; // Получаем текущий элемент
            this.idEl++; // Увеличиваем индекс для следующего вызова
            // Если текущий индекс равен вместимости контейнера, переходим к следующему контейнеру
            if (this.idEl == contCapacity && this.iterContainer.getNext() != null){
                this.idEl = 0; // Сбрасываем индекс
                this.iterContainer = (Container<T>) this.iterContainer.getNext(); // Переход к следующему контейнеру
            }
            return this.element; // Возвращаем текущий элемент
        } else {
             throw new NoSuchElementException("Кончились элементы"); // Если элементов больше нет, выбрасываем исключение
        }
    }
}
