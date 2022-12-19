package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class UserProvider {

    private static final String[] names = {
        "Naima Bryan", "Theodore Hewitt", "Ciara Macdonald", "Jayson Butler",
        "Ellie-Mae Wallace", "Pearl Daniel", "Karl Montgomery", "Victor Wolf",
        "Momeo Edwards", "Alma Snow", "Mazel Evans", "Lewis Gregory",
        "Connie Webb", "Sharon Connor", "Mdil Graves", "Brooklyn Bailey",
        "Elisa Price", "Made Huff", "Anaya Mckenzie", "Jonty Booth",
        "Alice David", "Louie Oconnell", "Henry Glenn", "Genevieve Wise",
        "Juan Farmer", "Ronan Archer", "Maksymilian Prince", "Saarah Sandoval",
        "Ela Stein", "Ali Atkins", "Maizie Roy", "Tasneem Fleming",
        "Penny Solis", "Muhammed Brady", "Aamina Hanna", "Aliyah Hicks",
        "Maryam Ball", "Khadijah Hahn", "Joel Lin", "Leyla Ortega", "Kezia Mcpherson", "Yousuf Robbins",
        "Jean Combs", "Clayton Reid", "Nettie Kirby", "Antony Proctor", "Madeleine Nelson",
        "Conner Ochoa", "Albie Simon", "Nikita Campos", "Elodie Acevedo",
        "Aya Finch", "Tahlia Holman", "Mya Yang", "Tom Underwood",
        "Miana Boyle", "Jake Webb", "Jannat Mcguire", "Michael Pitts",
        "Melvin Bailey", "Clyde Holder", "Stephen Butler", "Kayla Santana",
        "Junior Newton", "Serena Oconnell", "Asa Dillon", "Dana Buckley", "Abraham Benjamin",
        "Anton Hood", "Autumn Sykes", "Riya Watkins", "Mohamad Barron",
        "Chelsea Cotton", "Sabrina Rowe", "Damien Golden", "Haris John",
        "Krystal Carney", "Byron Humphrey", "Eleni Saunders", "Miriam Davila",
        "Gracie Huff", "Emilio Ryan", "Benedict Garza", "Emilia Lawrence",
        "Matie Holmes", "Eliot Curry", "Milly-May Arroyo", "Jane Griffith",
        "Isabella Clark", "Dalton Berry",
    };

    private static ApplicationUser.Role[] roles = ApplicationUser.Role.values();
    private static ApplicationUser.Gender[] genders = ApplicationUser.Gender.values();
    private static Random rd = new Random();

    public static List<ApplicationUser> getParticipants(int amount) {
        ArrayList<ApplicationUser> users = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            users.add(getRandomAppUser());
        }
        return users;
    }

    public static ApplicationUser getRandomAppUser() {
        String name = names[rd.nextInt(names.length)];
        String[] nameParts = name.split(" ");

        return new ApplicationUser(
            roles[rd.nextInt(roles.length)],
            nameParts[0], nameParts[1],
            genders[rd.nextInt(genders.length)],
            new Date(rd.nextInt(70) + 1950 - 1900,
                rd.nextInt(12) + 1,
                rd.nextInt(27) + 1),
            ""
        );
    }
}
