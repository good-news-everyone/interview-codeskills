import java.util.List;

/**
 * Необходимо найти человека с минимальным возрастом в комнате с максимальным количеством человек
 * В конце метода solution нужно вызвать метод writeSolution(user, room)
 */
class FindUser {

    public static void main(String[] args) {
        List<Room> rooms = List.of();
        solution(rooms);
    }

    private static void solution(List<Room> rooms) {
        // TODO
        // writeSolution(user, room)
    }

    private static void writeSolution(User user, Room room) {
        System.out.printf("Комната номер %s. Пользователь %s %s возраста %s%n", room.getNumber(), user.getFirstName(), user.getLastName(), user.getAge());
    }

    class User {
        private int age;
        private String firstName;
        private String lastName;

        public int getAge() {
            return this.age;
        }

        public String getFirstName() {
            return this.firstName;
        }

        public String getLastName() {
            return this.lastName;
        }
    }

    class Room {
        private int number;
        private List<User> users;

        public int getNumber() {
            return this.number;
        }

        public List<User> getUsers() {
            return this.users;
        }
    }
}
