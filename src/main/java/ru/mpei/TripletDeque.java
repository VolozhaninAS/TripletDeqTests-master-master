package ru.mpei;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TripletDeque<T> implements Deque<T>, Containerable {

    private Container<T> firstContainer;    // Первый контейнер
    private Container<T> lastContainer; // Последний контейнер
    private int size;   // Кол-во элементов в общем
    private int maxSize = 1000; // Максимальное кол-во элементов, которое может содержаться
    private int containerCapacity = 5;  // Размер контейнера
    private int current_addFirst;
    private int current_addLast;

    public TripletDeque() {
        Container<T> container = new Container<>(containerCapacity);
        this.firstContainer = container;
        this.lastContainer = container;
    }

    public TripletDeque(int containerCapacity, int maxSize) {    // Можно задать размер контейнера и очереди
        this.containerCapacity = containerCapacity;
        this.maxSize = maxSize;
        Container<T> container = new Container<>(containerCapacity);
        this.firstContainer = container;
        this.lastContainer = container;
    }

    @Override
    //объект данных добавляется в последний контейнер, в первую свободную ячейку слева.
    public void addLast(T element) {

        if (element == null)
            throw new NullPointerException("Попытка добавления null");

        if (size >= maxSize) {
            throw new IllegalStateException("Превышен размер очереди");
        }
        if (current_addLast == 0 && lastContainer.getLastElement() == 0){

            lastContainer.setArray(0, element);
            current_addLast++;
        } else {
            // Если последний контейнер заполнен, добавляем новый контейнер и пере привязываем ссылки
            if (lastContainer.getLastElement() >= containerCapacity - 1) {
                Container<T> newContainer = new Container<>(containerCapacity);
                newContainer.setPrev(lastContainer);
                lastContainer.setNext(newContainer);
                lastContainer = newContainer;
                lastContainer.setLastElement(0);
            }
            if (size == 0 || lastContainer.getArray()[lastContainer.getLastElement()] == null){
                lastContainer.setLastElement(0);
                lastContainer.setArray(lastContainer.getLastElement(),element);

            } else{
                lastContainer.setArray(lastContainer.getLastElement() + 1, element);
                lastContainer.setLastElement(lastContainer.getLastElement() + 1);
                current_addLast++;
            }
        }
        size++;
    }

    @Override
    //объект данных добавляется в последний контейнер, в первую свободную ячейку справа.
    public void addFirst(T element) {
        if (element == null)
            throw new NullPointerException("Попытка добавления null");
        if (size >= maxSize) {
            throw new IllegalStateException("Превышен размер очереди");
        }
        //Проверяет, не равен ли элемент null, и бросает NullPointerException, если это так
        if (current_addFirst == 0 ){
            firstContainer.setFirstElement(containerCapacity-1); // Новый контейнер начинается с конца
            firstContainer.setLastElement(containerCapacity-1); // Новый контейнер начинается с конца
            // добавляем элемент
            firstContainer.setArray(firstContainer.getFirstElement(), element);
            size++;
            current_addFirst++;
        } else {
            // Если первый контейнер заполнен, создаём новый контейнер и связываем с первым
            if (firstContainer.getFirstElement() == 0) {
                Container<T> newContainer = new Container<>(containerCapacity);
                newContainer.setNext(firstContainer);
                firstContainer.setPrev(newContainer);
                firstContainer = newContainer;
                firstContainer.setFirstElement(containerCapacity); // Новый контейнер начинается с конца
                firstContainer.setLastElement(containerCapacity-1);
            }
            // Уменьшаем индекс firstElement и добавляем элемент
            firstContainer.setFirstElement(firstContainer.getFirstElement() - 1);
            firstContainer.setArray(firstContainer.getFirstElement(), element);
            size++;
            current_addFirst++;

        }

    }

    @Override
    // Метод для добавления элемента в начало.
    public boolean offerFirst(T element) {
        try {
            addFirst(element);
            return true; // Возвращает true, если элемент был успешно добавлен,
        } catch (IllegalStateException e) {
            return false; // и false, если добавление не удалось из-за превышения максимального размера.
        }
    }

    @Override
    // Метод для добавления элемента в конец
    public boolean offerLast(T element) {
        try {
            addLast(element);
            return true; // Возвращает true, если элемент был успешно добавлен,
        } catch (IllegalStateException e) {
            return false; // и false, если добавление не удалось из-за превышения максимального размера.
        }
    }

    @Override
    // Метод для удаления и возврата первого элемента
    public T removeFirst() {
        if (size == 0) { // Проверяем, пуст ли
            throw new NoSuchElementException("Очередь пуста");
        }
        if (firstContainer.getArray()[firstContainer.getFirstElement()] == null){
            firstContainer.setFirstElement(0); // Если первый элемент равен null, устанавливаем индекс первого элемента в 0.
        }
        T element = (T) firstContainer.getArray()[firstContainer.getFirstElement()];
        firstContainer.setArray(firstContainer.getFirstElement(), null); // Получаем элемент для возврата и очищаем его из контейнера.
        // Проверяем, можем ли сместить первый элемент вправо.
        if (firstContainer.getFirstElement() != containerCapacity - 1 && firstContainer.getArray()[firstContainer.getFirstElement() + 1] != null) {
            firstContainer.setFirstElement(firstContainer.getFirstElement() + 1); // Сдвигаем первый элемент.
            firstContainer.setLastElement(firstContainer.getLastElement() - 1); // Уменьшаем количество элементов.
        }
        else {
            // Переход к следующему контейнеру, если первый контейнер пуст.
            if(firstContainer.getNext() != null){
                T next = (T) firstContainer.getNext();
                this.firstContainer.setNext(null); // Обнуляем ссылку на текущий контейнер.
                this.firstContainer = (Container<T>) next; // Переходим к следующему контейнеру.
                this.firstContainer.setPrev(null); // Обнуляем ссылку на предыдущий контейнер.
            }
            firstContainer.setLastElement(0); // Обновляем индекс последнего элемента.
        }
        size--; // Уменьшаем размер
        return element; // Возвращаем удаленный элемент.
    }

    @Override
    // Метод для удаления и возврата последнего элемента из дека.
    public T removeLast() {
        if (size == 0) { // Проверяем, пуст ли
            throw new NoSuchElementException("Очередь пуста"); // Если дек пуст, выбрасываем исключение.
        }
        // Получаем последний элемент и очищаем его из контейнера.
        T element = (T) lastContainer.getArray()[lastContainer.getLastElement()];
        lastContainer.setArray(lastContainer.getLastElement(), null);
        // Проверяем, можем ли сместить последний элемент влево.
        if (lastContainer.getLastElement() != 0 && lastContainer.getArray()[lastContainer.getLastElement() - 1] != null){
            lastContainer.setLastElement(lastContainer.getLastElement()-1); // Сдвигаем последний элемент.
        } else {
            // Переход к предыдущему контейнеру, если последний контейнер пуст.
            if(lastContainer.getPrev() != null){
                T prev = (T) lastContainer.getPrev();
                this.lastContainer.setPrev(null); // Обнуляем ссылку на текущий контейнер.
                this.lastContainer = (Container<T>) prev; // Переходим к предыдущему контейнеру.
                this.lastContainer.setNext(null); // Обнуляем ссылку на следующий контейнер.
            }
            lastContainer.setFirstElement(containerCapacity); // Обновляем индекс первого элемента.
        }
        size--; // Уменьшаем размер
        return element; // Возвращаем удаленный элемент.
    }

    @Override
    // Метод для безопасного удаления и возврата последнего элемента из дека без исключений.
    public T pollLast() {
        if (size == 0) { // Проверяем, пуст ли
            return null; // Если пкст, то возвращаем null
        }
        // Получаем последний элемент и очищаем его из контейнера.
        T element = (T) lastContainer.getArray()[lastContainer.getLastElement()];
        lastContainer.setArray(lastContainer.getLastElement(), null);
        // Проверяем, можем ли сместить последний элемент влево.
        if (lastContainer.getLastElement() != 0 && lastContainer.getArray()[lastContainer.getLastElement() - 1] != null){
            lastContainer.setLastElement(lastContainer.getLastElement()-1); // Сдвигаем последний элемент
        } else {
            // Переход к предыдущему контейнеру, если последний контейнер пуст
            if(lastContainer.getPrev() != null){
                T prev = (T) lastContainer.getPrev();
                this.lastContainer.setPrev(null); // Обнуляем ссылку на текущий контейнер
                this.lastContainer = (Container<T>) prev; // Переходим к предыдущему контейнеру
                this.lastContainer.setNext(null); // Обнуляем ссылку на следующий контейнер
            }
            lastContainer.setFirstElement(containerCapacity); // Обновляем индекс первого элемента
        }
        size--; // Уменьшаем размер
        return element; // Возвращаем удаленный
    }

    @Override
    // Метод для безопасного получения первого элемента без его удаления
    public T pollFirst() {
        if (size == 0) { // Проверяем, пуст ли
            return null; // Если пуст, возвращаем null.
        }
        // Если первый элемент равен null, устанавливаем индекс первого элемента в 0
        if (firstContainer.getArray()[firstContainer.getFirstElement()] == null){
            firstContainer.setFirstElement(0);
        }
        // Получаем элемент для возврата и очищаем его из контейнера
        T element = (T) firstContainer.getArray()[firstContainer.getFirstElement()];
        firstContainer.setArray(firstContainer.getFirstElement(), null);
        // Проверяем, можем ли сместить первый элемент вправо
        if (firstContainer.getFirstElement() != containerCapacity - 1 &&
                firstContainer.getArray()[firstContainer.getFirstElement() + 1] != null) {
            firstContainer.setFirstElement(firstContainer.getFirstElement()+1); // Сдвигаем первый элемент
            firstContainer.setLastElement(firstContainer.getLastElement()-1); // Уменьшаем количество элементов
        } else {
            // Переход к следующему контейнеру, если первый контейнер пуст
            if(firstContainer.getNext() != null){
                T next = (T) firstContainer.getNext();
                this.firstContainer.setNext(null); // Обнуляем ссылку на текущий контейнер
                this.firstContainer = (Container<T>) next; // Переходим к следующему контейнеру
                this.firstContainer.setPrev(null); // Обнуляем ссылку на предыдущий контейнер
            }
            firstContainer.setLastElement(0); // Обновляем индекс последнего элемента
        }
        size--; // Уменьшаем размер
        return element; // Возвращаем элемент
    }

    @Override
    // Метод для получения первого элемента без удаления
    public T getFirst() {
        if (size == 0) { // Проверяем, пуст ли
            throw new NoSuchElementException("Элементов нет"); // Если дек пуст, выбрасываем исключение
        } else {
            // Получаем массив элементов и возвращаем первый элемент
            T[] array = (T[]) firstContainer.getArray();
            return array[firstContainer.getFirstElement()];
        }
    }

    @Override
    // Метод для получения последнего элемента без его удаления.
    // Если дек пуст, выбрасывается исключение NoSuchElementException.
    public T getLast() {
        if (size == 0){
            throw new NoSuchElementException("Элементов нет"); // пуст -- выбрасываем исключение
        } else{
            T[] array = (T[]) lastContainer.getArray(); // Получаем массив элементов из последнего контейнера
            return array[lastContainer.getLastElement()]; // Возвращаем последний элемент
        }
    }

    @Override
    // Метод для получения первого элемента без его удаления.
    // Возвращает элемент или null, если контейнер пуст
    public T peekFirst() {
        return firstContainer.getArray()[firstContainer.getFirstElement()];
    } // Возвращаем первый элемент из контейнера

    @Override
    // Метод для получения последнего элемента без его удаления.
    // Возвращает элемент или null, если контейнер пуст.
    public T peekLast() {
        return lastContainer.getArray()[lastContainer.getLastElement()];
    } // Возвращаем последний элемент из контейнера

    // Удаляет первое появление элемента слева.
    // При удалении остальные элементы контейнера сдвигаются влево, чтобы не было дырок с null,
    // а в конце добавляется null.
    @Override
    public boolean removeFirstOccurrence(Object o) {
        Container<T> current = firstContainer; // Начинаем с первого контейнера
        while (current != null) { // Перебираем все контейнеры
            for (int i = 0; i < containerCapacity; i++) { // Перебираем элементы внутри контейнера
                if (o.equals(current.getArray()[i])) { // Находим элемент для удаления
                    // Сдвигаем элементы влево, чтобы закрыть пробел
                    for (int j = i; j < containerCapacity - 1; j++) {
                        current.getArray()[j] = current.getArray()[j+1];
                    }
                    current.setArray(current.getLastElement(), null); // Очищаем последний элемент
                    current.setLastElement(current.getLastElement() - 1); // Уменьшаем количество элементов в контейнере
                    size--; // Уменьшаем общий размер
                    // Проверяем, если контейнер стал пустым
                    if (current.getLastElement() == -1) {
                        // Обновляем указатели, если текущий контейнер является первым или последним
                        if (current == firstContainer) {
                            firstContainer = current.getNext(); // Перемещаем на следующий контейнер
                        }
                        if (current == lastContainer) {
                            lastContainer = current.getPrev(); // Перемещаем на предыдущий контейнер
                        }
                        // Обновляем ссылки между контейнерами
                        if (current.getPrev() != null) {
                            current.getPrev().setNext(current.getNext());
                        }
                        if (current.next != null) {
                            current.getNext().setPrev(current.getPrev());
                        };
                    }
                    return true; // Успешное удаление элемента
                }
            }
            current = current.getNext(); // Переход к следующему контейнеру
        }
        return false; // Элемент не найден
    }

    // Удаляет первое появление элемента справа.
    // При удалении остальные элементы контейнера сдвигаются влево, чтобы не было дырок с null,
    // а в конце добавляется null.
    @Override
    public boolean removeLastOccurrence(Object o) {
        Container<T> current = firstContainer; // Начинаем с первого контейнера
        while (current != null) { // Перебираем все контейнеры
            for (int i = containerCapacity - 1; i >= 0; i--) { // Перебираем элементы с конца контейнер
                if (o.equals(current.getArray()[i])) { // Находим элемент для удаления
                    // Сдвигаем элементы влево, чтобы закрыть пробел
                    for (int j = i; j < containerCapacity - 1; j++) {
                        current.getArray()[j] = current.getArray()[j + 1];
                    }
                    // Очищаем последний элемент
                    current.getArray()[current.getLastElement() - 1] = null;
                    current.setLastElement(current.getLastElement() - 1); // Уменьшаем количество элементов в контейнере
                    size--; // Уменьшаем общий размер
                    return true; // Успешное удаление элемента
                }
            }
            current = current.getNext(); // Переход к следующему контейнеру
        }
        return false; // Элемент не найден
    }

    @Override
    // Метод для добавления элемента в дек.
    // Возвращает true, если элемент успешно добавлен,
    // и false, если добавление не удалось из-за превышения максимального размера.
    public boolean add(T t) {
        try {
            addLast(t); // Пытаемся добавить элемент в конец
            return true; // Если добавление завершилось успешно, возвращаем true
        } catch (IllegalStateException e) {
            return false; // Если произошло исключение, возвращаем false
        }
    }

    @Override
    // Метод для добавления элемента
    // Работает аналогично методу add, возвращает false при ошибке.
    public boolean offer(T t) {
        try {
            addLast(t); // Пытаемся добавить элемент в конец
            return true; // Если добавление завершилось успешно, возвращаем true
        } catch (IllegalStateException e) {
            return false; // Если произошло исключение, возвращаем false
        }
    }

    @Override
    // Метод для удаления и возврата первого элемента
    public T remove() {
        if (size == 0) { // Проверка на пустоту
            throw new NoSuchElementException("Очередь пуста"); // Если пуст, выбрасываем исключение
        }
        // Если первый элемент равен null, устанавливаем индекс первого элемента в 0
        if (firstContainer.getArray()[firstContainer.getFirstElement()] == null){
            firstContainer.setFirstElement(0);
        }
        T element = (T) firstContainer.getArray()[firstContainer.getFirstElement()]; // Получаем первый элемент
        firstContainer.setArray(firstContainer.getFirstElement(), null); // Очищаем его из контейнера
        // Проверяем, можем ли сдвинуть первый элемент вправо
        if (firstContainer.getFirstElement() != containerCapacity - 1 && firstContainer.getArray()[firstContainer.getFirstElement() + 1] != null) {
            firstContainer.setFirstElement(firstContainer.getFirstElement() + 1); // Сдвигаем индекс первого элемента
            firstContainer.setLastElement(firstContainer.getLastElement() - 1); // Уменьшаем количество элементов
        } else {
            // Переход к следующему контейнеру, если первый контейнер пуст
            if (firstContainer.getNext() != null){
                T next = (T) firstContainer.getNext();
                this.firstContainer.setNext(null); // Обнуляем ссылку на текущий контейнер
                this.firstContainer = (Container<T>) next; // Переходим к следующему контейнеру
                this.firstContainer.setPrev(null); // Обнуляем ссылку на предыдущий контейнер
            }
            firstContainer.setLastElement(0); // Обновляем индекс последнего элемента
        }
        size--; // Уменьшаем размер
        return element; // Возвращаем удаленный элемент
    }

    @Override
    // Метод для безопасного удаления и возврата первого элемента из дека без исключений
    public T poll() {
        if (size == 0) { // Проверка пустоты
            return null; // Если пуст, возвращаем null
        }
        // Если первый элемент равен null, устанавливаем индекс первого элемента в 0
        if (firstContainer.getArray()[firstContainer.getFirstElement()] == null){
            firstContainer.setFirstElement(0);
        }
        T element = (T) firstContainer.getArray()[firstContainer.getFirstElement()]; // Получаем элемент для возврата
        firstContainer.setArray(firstContainer.getFirstElement(), null); // Очищаем его из контейнера
        // Проверяем, можем ли сдвинуть первый элемент вправо
        if (firstContainer.getFirstElement() != containerCapacity - 1 && firstContainer.getArray()[firstContainer.getFirstElement() + 1] != null) {
            firstContainer.setFirstElement(firstContainer.getFirstElement() + 1); // Сдвигаем индекс первого элемента
            firstContainer.setLastElement(firstContainer.getLastElement()-1); // Уменьшаем количество элементов
        }  else {
            // Переход к следующему контейнеру, если первый контейнер пуст
            if(firstContainer.getNext() != null){
                T next = (T) firstContainer.getNext();
                this.firstContainer.setNext (null); // Обнуляем ссылку на текущий контейнер
                this.firstContainer = (Container<T>) next; // Переходим к следующему контейнеру
                this.firstContainer.setPrev(null); // Обнуляем ссылку на предыдущий контейнер
            }
            firstContainer.setLastElement(0); // Обновляем индекс последнего элемента
        }
        size--; // Уменьшаем размер
        return element; // Возвращаем удаленный элемент
    }

    @Override
    // Метод для получения первого элемента без удаления.
    // Если пуст, выбрасывается исключение NoSuchElementException
    public T element() {
        if (size == 0) { // Проверяем, пуст ли
            throw new NoSuchElementException("Элементов нет"); // Если пуст, выбрасываем исключение
        } else {
            T[] array = (T[]) firstContainer.getArray(); // Получаем массив элементов
            return array[firstContainer.getFirstElement()]; // Возвращаем первый элемент
        }
    }

    @Override
    // Метод для безопасного получения первого элемента без его удаления.
    // Возвращает null, если пуст
    public T peek() {
        return firstContainer.getArray()[firstContainer.getFirstElement()]; // Возвращаем первый элемент из контейнера
    }

    @Override
    // Метод для добавления всех элементов из коллекции
    // Возвращает true, если все элементы успешно добавлены,
    // и false, если добавление хотя бы одного элемента не удалось.
    public boolean addAll(Collection<? extends T> c) {
        Iterator<? extends T> iterator = c.iterator(); // Получаем итератор для коллекции
        while (iterator.hasNext()) { // Проходим по всем элементам коллекции
            T element = iterator.next(); // Получаем следующий элемент
            if (!offerLast(element)) { // Пытаемся добавить элемент в конец
                return false; // Если хотя бы одно добавление не удалось, возвращаем false
            }
        }
        return true; // Все элементы успешно добавлены
    }

    @Override
    // Метод для удаления всех элементов, которые содержатся в указанной коллекции.
    // В данной реализации метод не выполняет никаких действий и всегда возвращает false.
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    // Метод для удаления из дека всех элементов, которые не содержатся в указанной коллекции.
    // В данной реализации метод не выполняет никаких действий и всегда возвращает false.
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    // Метод для очистки дека, обнуляет все элементы и устанавливает новый контейнер.
    // Теперь будет пусто(
    public void clear() {
        Container<T> container = new Container<>(containerCapacity); // Создаем новый контейнер
        this.firstContainer = container; // Устанавливаем его в качестве первого контейнера
        this.lastContainer = container; // Устанавливаем его в качестве последнего контейнера
        size = 0; // Устанавливаем размер в 0
    }

    @Override
    // Метод для добавления элемента в стек (в начало).
    // Если элемент равен null, выбрасывается NullPointerException.
    // Если размер дека превышает максимум, выбрасывается IllegalStateException
    public void push(T t) {
        if (t == null)
            throw new NullPointerException("Попытка добавления null"); // Проверка на null
        if (size >= maxSize) {
            throw new IllegalStateException("Превышен размер очереди"); // Проверка максимального размера
        }
        // Если индекс текущего элемента равен 0, значит, мы добавляем элемент в новый контейнер
        if (current_addFirst == 0 ){
            firstContainer.setFirstElement(containerCapacity-1); // Новый контейнер начинается с конца
            firstContainer.setLastElement(containerCapacity-1); // Новый контейнер начинается с конца
            firstContainer.setArray(firstContainer.getFirstElement(), t); // добавляем элемент
            size++; // Увеличиваем размер
            current_addFirst++; // Увеличиваем индекс добавления в первый контейнер
        } else {
            // Если первый контейнер заполнен, создаём новый контейнер и связываем с первым
            if (firstContainer.getFirstElement() == 0) {
                Container<T> newContainer = new Container<>(containerCapacity);
                newContainer.setNext(firstContainer); // Новые связи контейнеров
                firstContainer.setPrev(newContainer);
                firstContainer = newContainer; // Переходим к новому контейнеру
                firstContainer.setFirstElement(containerCapacity); // Новый контейнер начинается с конца
                firstContainer.setLastElement(containerCapacity - 1); // Новый контейнер начинается с конца
            }
            // Уменьшаем индекс firstElement и добавляем элемент
            firstContainer.setFirstElement(firstContainer.getFirstElement() - 1);
            firstContainer.setArray(firstContainer.getFirstElement(), t); // Добавляем элемент
            size++; // Увеличиваем размер
            current_addFirst++; // Увеличиваем счетчик добавлений
        }
    }

    @Override
    // Метод для удаления и возврата последнего добавленного элемента
    // Если пуст, выбрасывается исключение NoSuchElementException.
    public T pop() {
        if (size == 0) {
            throw new NoSuchElementException("Очередь пуста"); // Проверяем, пусто ли
        }
        // Если первый элемент равен null, устанавливаем индекс первого элемента в 0
        if (firstContainer.getArray()[firstContainer.getFirstElement()] == null){
            firstContainer.setFirstElement(0);
        }
        T element = (T) firstContainer.getArray()[firstContainer.getFirstElement()]; // Получаем элемент
        firstContainer.setArray(firstContainer.getFirstElement(), null); // Очищаем его из контейнера
        // Проверяем, можем ли сдвинуть первый элемент вправо
        if (firstContainer.getFirstElement() != containerCapacity - 1 && firstContainer.getArray()[firstContainer.getFirstElement() + 1] != null) {
            firstContainer.setFirstElement(firstContainer.getFirstElement() + 1); // Сдвигаем индекс
            firstContainer.setLastElement(firstContainer.getLastElement() - 1); // Уменьшаем количество элементов
        } else {
            // Переход к следующему контейнеру, если первый контейнер пуст
            if (firstContainer.getNext() != null) {
                T next = (T) firstContainer.getNext();
                this.firstContainer.setNext(null); // Обнуляем ссылку на текущий контейнер.
                this.firstContainer = (Container<T>) next; // Переходим к следующему контейнеру
                this.firstContainer.setPrev(null); // Обнуляем ссылку на предыдущий контейнер
            }
            firstContainer.setLastElement(0); // Обновляем индекс последнего элемента
        }
        size--; // Уменьшаем размер
        return element; // Возвращаем удаленный элемент
    }

    @Override
    // Метод для удаления первого вхождения указанного элемента.
    // Если элемент найден, возвращает true, в противном случае возвращает false
    public boolean remove(Object o) {
        Container<T> current = firstContainer; // Начинаем с первого контейнера
        while (current != null) { // Перебираем все контейнеры
            for (int i = 0; i < containerCapacity; i++) { // Перебираем элементы внутри контейнера
                if (o.equals(current.getArray()[i])) { // Находим элемент для удаления
                    // Сдвигаем элементы влево, чтобы закрыть пробел
                    for (int j = i; j < containerCapacity - 1; j++) {
                        current.getArray()[j] = current.getArray()[j+1];
                    }
                    current.setArray(current.getLastElement(), null); // Очищаем последний элемент
                    current.setLastElement(current.getLastElement() - 1); // Уменьшаем количество элементов
                    size--; // Уменьшаем общий размер
                    // Проверяем, если контейнер стал пустым
                    if (current.getLastElement() == -1){
                        if (current == firstContainer) {
                            firstContainer = current.getNext(); // Обновляем указатель на первый контейнер
                        }
                        if (current == lastContainer) {
                            lastContainer = current.getPrev(); // Обновляем указатель на последний контейнер
                        }
                        // Обновляем ссылки между контейнерами
                        if (current.getPrev() != null) {
                            current.getPrev().setNext(current.getNext());
                        }
                        if (current.next != null) {
                            current.getNext().setPrev(current.getPrev());
                        };
                    }
                    return true; // Успешное удаление элемента
                }
            }
            current = current.getNext(); // Переход к следующему контейнеру
        }
        return false; // Элемент не найден
    }

    @Override
    // Метод для проверки, содержит ли очередь все элементы из указанной коллекции.
    // В данной реализации метод не выполняет никаких действий и всегда возвращает false.
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    // Проверяет, содержится ли указанный элемент в очереди.
    // Перебирает все контейнеры и их элементы, возвращает true при нахождении элемента.
    public boolean contains(Object elements) {
        Container<T> current = firstContainer; // Начинаем с первого контейнера
        while (current != null) { // Перебираем контейнеры
            for (int i = 0; i < containerCapacity; i++) { // Перебираем элементы внутри контейнера
                if (elements.equals(current.getArray()[i])) { // Сравниваем с элементами
                    return true; // Если элемент найден, возвращаем true
                }
            }
            current = current.next; // Переход к следующему контейнеру
        }
        return false; // Если элемент не найден, возвращаем false
    }

    @Override
    // Метод для получения текущего размера очереди
    public int size() {
        return size; // Возвращаем количество элементов в очереди
    }

    @Override
    // Метод для проверки, пуста ли очередь.
    // Возвращает true, если размер равен 0; иначе возвращает false.
    public boolean isEmpty() {
        if (size == 0) {
            return true; // Возвращаем true, если размер равен 0
        } else {
            return false;
        }
    }

    @Override
    // Метод для получения итератора элементов очереди.
    // Возвращает новый экземпляр итератора, инициализированный первым контейнером.
    public Iterator<T> iterator() {
        NewIterator iterator = new NewIterator (firstContainer, containerCapacity); // Создаем новый итератор
        return iterator; // Возвращаем итератор
    }

    @Override
    // Метод для преобразования очереди в массив.
    // В данной реализации возвращает пустой массив.
    public Object[] toArray() {
        return new Object[0]; // Возвращаем новый пустой массив
    }

    @Override
    // Метод для преобразования очереди в массив, принимая массив нужного типа в качестве параметра.
    // В данной реализации возвращает null.
    public <T1> T1[] toArray(T1[] a) {
        return null; // Не реализована логика преобразования в массив указанного типа
    }

    @Override
    // Метод для получения итератора в обратном порядке.
    // В данной реализации возбуждает UnsupportedOperationException, так как метод не реализован.
    public Iterator<T> descendingIterator() {
        throw new UnsupportedOperationException("Метод не реализован"); // Исключение при вызове несуществующего метода.
    }

    // Метод для получения содержимого контейнера по указанному индексу.
    // Возвращает массив содержимого контейнера или null, если контейнер не найден.
    public Object[] getContainerByIndex(int cIndex) {
        Container<T> current = firstContainer; // Начинаем с первого контейнера.
        int currentIndex = 0; // Индекс текущего контейнера.
        // Идем по контейнерам до нужного индекса
        while (current != null && currentIndex < cIndex) {
            current = current.next; // Переход к следующему контейнеру.
            currentIndex++; // Увеличиваем индекс.
        }
        if (current != null) {
            // Возвращаем содержимое контейнера, если нашли нужный контейнер
            return current.getArray(); // Возвращаем массив элементов контейнера.
        }
        return null; // Если контейнер не найден, возвращаем null.
    }
}