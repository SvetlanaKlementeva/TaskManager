package src.model.packet;

public enum State {
    OK,
    ERROR, LOGIN_ERROR,
    //Сервер возвращает если у клиента нет ещё задач
    NO_TASKS
}
