package com.company;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Interface representing states of contest
 */
interface ContestState {
    /**
     * @param photoContest - concrete contest
     */
    void nextState(PhotoContest photoContest);
}

/**
 * Contest state, during which photographers can register and send applications for contest
 */
class ContestApplication implements ContestState {

    /**
     * Transaction to next state: Plagiarism checking
     *
     * @param photoContest - concrete contest
     */
    @Override
    public void nextState(PhotoContest photoContest) {
        photoContest.contestState = new ContestChoice();
    }
}

/**
 * Contest state, during which admin checking for plagiarism
  */
class ContestChoice implements ContestState {

    /**
     * Transaction to next state: Voting
     *
     * @param photoContest - concrete contest
     */
    @Override
    public void nextState(PhotoContest photoContest) {
        photoContest.contestState = new ContestVote();
    }
}

/**
 * Contest state, during which vote is opened
 */
class ContestVote implements ContestState {

    /**
     * Transaction to next state: Awarding ceremony
     *
     * @param photoContest - concrete contest
     */
    @Override
    public void nextState(PhotoContest photoContest) {
        photoContest.contestState = new ContestAwarding();
    }
}

/**
 * Contest state, during which the winner is determined
 */
class ContestAwarding implements ContestState {

    /**
     * Transaction to next state: Closure of contest
     * And outputs that contest is closed
     *
     * @param photoContest - concrete contest
     */
    @Override
    public void nextState(PhotoContest photoContest) {
        photoContest.contestState = new ContestClosed();
        System.out.println("Contest is closed.");
    }
}

/**
 * Contest state, during which the contest is closed
 */
class ContestClosed implements ContestState {

    /**
     * Outputs that contest is closed
     *
     * @param photoContest - concrete contest
     */
    @Override
    public void nextState(PhotoContest photoContest) {
        System.out.println("Contest is closed.");
    }
}

/**
 * Class represents a singular photo contest
 */
class PhotoContest {
    ContestState contestState;
    private final ArrayList<Observer<ContestState>> photographersList = new ArrayList<>();
    int winnerRate;

    PhotoContest() {
        contestState = new ContestApplication();
    }

    /**
     * Add subscriber to the list of photographers
     * and change state of photographer to Registered
     *
     * @param subscriber - concrete Photographer
     */
    public void subscribe(Photographer subscriber) {
        subscriber.accepted();
        photographersList.add(subscriber);
    }

    /**
     * Getter for photographers list
     *
     * @return array list of photographers
     */
    public ArrayList<Observer<ContestState>> getPhotographersList() {
        return photographersList;
    }

    /**
     * Automatic notification for subscribers
     */
    public void notification() {

        //For each observer from list of photographers notification is sent
        for (Observer<ContestState> x :
                photographersList) {
            x.notification(contestState);
        }
    }

    /**
     * Current state of Contest is finished, change to the next one
     */
    public void deadline() {
        contestState.nextState(this);
    }
}

/**
 * Interface representing states of photographer
 */
interface PhotographerState {
    /**
     * Transition in case of failure. Transitions:
     *
     * @param photographer - concrete photographer
     */
    void failed(Photographer photographer);

    /**
     *
     * @param photographer - concrete photographer
     */
    void accepted(Photographer photographer);

}

class Failure implements PhotographerState {

    @Override
    public void failed(Photographer photographer) {

    }

    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new Initial());
    }
}

class Initial implements PhotographerState {

    @Override
    public void failed(Photographer photographer) {

    }

    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new Registration());
    }
}

class Registration implements PhotographerState {

    @Override
    public void failed(Photographer photographer) {
        photographer.setState(new Failure());
    }

    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new Application());
    }
}

class Application implements PhotographerState {

    @Override
    public void failed(Photographer photographer) {
        photographer.setState(new Failure());
    }

    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new Contest());
    }
}

class Contest implements PhotographerState {

    @Override
    public void failed(Photographer photographer) {
        photographer.setState(new Failure());
    }

    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new Top());
    }
}

class Top implements PhotographerState {

    @Override
    public void failed(Photographer photographer) {

    }

    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new Initial());
    }
}

class Photographer implements Observer<ContestState> {
    private PhotographerState state;
    String name;
    String photo;
    String email;
    String phoneNumber;
    String notifyData;
    boolean accepted;
    int rate;

    private Photographer(String name) {
        this.state = new Initial();
        this.name = name;
        photo = null;
        accepted = false;
        rate = 0;
    }

    Photographer(String name, String s) {
        this(name);
        if (s.contains("@"))
            email = s;
        else
            phoneNumber = s;
        setNotification();
        System.out.println(notifyData + "You have been successfully discovered as a photographer");
    }

    Photographer(String name, String mail, String phone) {
        this(name);
        email = mail;
        phoneNumber = phone;
        setNotification();
        System.out.println(notifyData + "You have been successfully discovered as a photographer");
    }

    Photographer() {
        this(null);
        System.out.println("Hello, new photographer!");
        Scanner scan = new Scanner(System.in);
        while (name == null) {
            System.out.println("Please, enter your name:");
            name = scan.nextLine();
        }
        System.out.println(name + ", how to contact with you? 1/2/3");
        System.out.println("1 - email, 2 - phone, 3 - both email and phone.");
        String line;
        label:
        while (true) {
            line = scan.nextLine();
            switch (line) {
                case "1":
                    setEmail(scan);
                    break label;
                case "2":
                    setPhone(scan);
                    break label;
                case "3":
                    setEmail(scan);
                    setPhone(scan);
                    break label;
                default:
                    System.out.println("Choose one of the following options: ");
                    System.out.println("1 - email, 2 - phone, 3 - both email and phone.");
                    break;
            }
        }
        setNotification();
        System.out.println(notifyData + "You have been successfully discovered as a photographer");
    }

    private void setEmail(Scanner scan) {
        System.out.println("Now, enter your email: ");
        while (!setEmail(scan.nextLine())) {
            System.out.println("Try to enter your email again, please: ");
        }
    }

    private void setPhone(Scanner scan) {
        System.out.println("Lets set your phone number: ");
        while (!setPhoneNumber(scan.nextLine())) {
            System.out.println("Try to enter your phone number again, please: ");
        }
    }

    private boolean setPhoneNumber(String newPhone) {
        if (newPhone.equals("")) {
            System.out.println("Oh, you didn't enter your phone number!");
            return false;
        } else {
            phoneNumber = "";
            for (char c : newPhone.toCharArray()) {
                if ("0123456789".contains(String.valueOf(c))) {
                    phoneNumber = phoneNumber.concat(String.valueOf(c));
                } else if (!"()- +".contains(String.valueOf(c))) {
                    System.out.println("Phone number can contain only '+','(',')','-' and digits");
                    return false;
                }
                if (phoneNumber.length() > 11) {
                    System.out.println("Phone number have to have exactly 11 digits");
                    return false;
                }
            }
        }
        if (phoneNumber.length() != 11) {
            System.out.println("Phone number have to have exactly 11 digits");
            return false;
        }
        return true;
    }

    private boolean setEmail(String newMail) {
        if (newMail.equals("")) {
            System.out.println("Oh, you didn't enter your email!");
            return false;
        } else if (!"qwertyuiopasdfghjklzxcvbnm".contains(newMail.toLowerCase().substring(0, 1))) {
            System.out.println("First letter have to start with the english character");
            return false;
        } else if (!newMail.contains("@")) {
            System.out.println("Email have to have an @");
            return false;
        } else {
            String[] st = newMail.split("@");
            if (st.length < 2 || st[1].length() < 3) {
                System.out.println("Email have to have at least one english letter before '.' and one after");
                return false;
            } else if (!st[1].contains(".")) {
                System.out.println("Email have to have an . after the @ sign");
                return false;
            }
            for (int i = 0; i < st[0].length(); i++) {
                if (!"qwertyuiopasdfghjklzxcvbnm1234567890_.".contains(newMail.substring(i, i + 1))) {
                    System.out.println("You can use only english letters, digits, '.' or '_' before the @ sign");
                    return false;
                }
            }
            for (int i = st[0].length() + 1; i < newMail.indexOf('.'); i++) {
                if (!"qwertyuiopasdfghjklzxcvbnm".contains(newMail.substring(i, i + 1))) {
                    System.out.println("You can use only english letters and only one . after the @ sign");
                    return false;
                }
            }
            for (int i = newMail.indexOf('.') + 1; i < newMail.length() - 1; i++) {
                if (!"qwertyuiopasdfghjklzxcvbnm".contains(newMail.substring(i, i + 1))) {
                    System.out.println("You can use only english letters or '.' after the @ sign");
                    return false;
                }
            }
            if (!"qwertyuiopasdfghjklzxcvbnm".contains(newMail.substring(newMail.length() - 1))) {
                System.out.println("You can use only english letters and only one '.' after the @ sign");
                return false;
            }
        }
        email = newMail;
        return true;
    }

    private void setNotification() {
        notifyData = "Notification for " + name + " was send to ";
        if (email != null && phoneNumber != null) {
            notifyData += email + " and " + phoneNumber;
        } else if (email != null) {
            notifyData += email;
        } else if (phoneNumber != null) {
            notifyData += phoneNumber;
        } else {
            notifyData = "Notification for " + name;
        }
        notifyData += ": ";
    }

    public void register(PhotoContest photoContest) {
        if (photoContest.contestState instanceof ContestApplication) {
            System.out.println("Photographer " + name + " successfully registered.");
            photoContest.subscribe(this);
        } else {
            System.out.println("Photographer " + name + " cannot register for the contest.");
        }
    }

    public void sendPhoto(String photo) {
        if (state instanceof Registration) {
            this.photo = photo;
            accepted();
            System.out.println(notifyData + "You successfully send a photo.");
        } else {
            System.out.println(notifyData + "You can't submit a photo.");
        }

    }

    public void sendPhoto() {
        if (state instanceof Registration) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Please, " + name + " enter name of the photo:");
            this.photo = scan.nextLine();
            accepted();
            System.out.println(notifyData + "You successfully send a photo.");
        } else {
            System.out.println(notifyData + "You can not submit a photo.");
        }

    }

    public PhotographerState getState() {
        return state;
    }

    public void setState(PhotographerState state) {
        this.state = state;
    }

    public void failed() {
        this.state.failed(this);
    }

    public void accepted() {
        this.state.accepted(this);
    }


    @Override
    public void notification(ContestState contestState) {
        if (contestState instanceof ContestChoice) {
            if (photo == null) {
                failed();
                System.out.println(notifyData + "You didn't submit a photo. You failed the contest.");
            } else {
                System.out.println(notifyData + "Your submission is on review.");
            }
        } else if (contestState instanceof ContestVote) {
            if (state instanceof Application) {
                if (accepted) {
                    accepted();
                    System.out.println(notifyData + "Your photo was accepted for voting.");
                } else {
                    failed();
                    System.out.println(notifyData + "You didn't pass the review session.");
                }
            }
        } else if (contestState instanceof ContestAwarding) {
            if (state instanceof Contest) {
                System.out.println(notifyData + "Your rate is " + rate + ".");
            } else if (state instanceof Top) {
                System.out.println(name + " is the winner!");
            }
        }
    }
}

interface Observer<T> {
    void notification(T t);
}

class Admin {
    private PhotoContest photoContest;
    String topic;
    ArrayList<Observer<ContestState>> photographersList;

    PhotoContest createNewContest(String topic) {
        System.out.println("New contest about '" + topic + "' is opened.");
        photoContest = new PhotoContest();
        this.topic = topic;
        return photoContest;
    }

    public void closeApplicationSession() {
        if (photoContest.contestState instanceof ContestApplication) {
            System.out.println("Application session for contest '" + topic + "' is closed");
            photoContest.deadline();
            photoContest.notification();
        }
    }

    public void peerReviewSession() {
        photographersList = photoContest.getPhotographersList();
        for (Object x :
                photographersList) {
            if (x instanceof Photographer && ((Photographer) x).getState() instanceof Application) {
//                if photographer went over plagiarism
                ((Photographer) x).accepted = plagiarismChecker((Photographer) x);
            }
        }
        photoContest.deadline();
        photoContest.notification();
    }

    boolean plagiarismChecker(Photographer photographer) {
        for (Object x : photographersList) {
            if (!x.equals(photographer) && x instanceof Photographer && (((Photographer) x).getState() instanceof Application || ((Photographer) x).getState() instanceof Contest)) {
                if (((Photographer) x).photo.equals(photographer.photo)) {
                    photographer.accepted = false;
                    ((Photographer) x).accepted = false;
                    return false;
                }
            }
        }
        return true;
    }

    public void votingSession() {
        int max = 0;
        System.out.println("Now we will vote to choose the best one!");
        for (Object x : photographersList) {
            if (x instanceof Photographer && ((Photographer) x).getState() instanceof Contest) {
                int rate = setRating((Photographer) x);
                ((Photographer) x).rate = rate;
                if (rate > max) {
                    photoContest.winnerRate = rate;
                    max = rate;
                }
            }
        }
        photoContest.deadline();
        photoContest.notification();
    }

    int setRating(Photographer photographer) {
        System.out.println("How many likes does " + photographer.photo + " have?");
        int rate = 0;
        Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();
        try {
            rate = Integer.parseInt(line);
        } catch (NumberFormatException exc) {
            System.out.println("Accepted only integers. Rating for " + photographer.photo + " is 0.");
        }
        return rate;
    }

    public void chooseWinner() {
        for (Object x : photographersList) {
            if (x instanceof Photographer && ((Photographer) x).getState() instanceof Contest) {
                if (((Photographer) x).rate == photoContest.winnerRate) {
                    ((Photographer) x).accepted();
                } else {
                    ((Photographer) x).failed();
                }
            }
        }
        photoContest.notification();
        photoContest.deadline();
    }
}

//public class Main {
public class Main {
    public static void main(String[] args) {
        Admin admin = new Admin();
        PhotoContest photoContest = admin.createNewContest("ЗМИЙ");
        System.out.println();

        ArrayList<Photographer> photographers = new ArrayList<>();

        photographers.add(new Photographer());
        photographers.add(new Photographer());
//        add photographers directly
        photographers.add(new Photographer("Georgy", "g@mail.ru"));
        photographers.add(new Photographer("Fedor", "89224224421"));
        photographers.add(new Photographer("Petr I", "velikiy@russia.rf", "00000000000"));
        photographers.add(new Photographer("Teacher", "t@t.t"));


//        PG0 registers and sends a photo
        photographers.get(0).register(photoContest);
        photographers.get(0).sendPhoto();
//      PG3 sends a photo before registration
        photographers.get(3).sendPhoto();
//        PG3 registers and sends a photo
        photographers.get(3).register(photoContest);
        photographers.get(3).sendPhoto();
//        Let PG2 & PG5 send the same photo
        photographers.get(1).register(photoContest);
        photographers.get(5).register(photoContest);
        photographers.get(5).sendPhoto("PlagiatedPhoto");
        photographers.get(1).sendPhoto("PlagiatedPhoto");
        System.out.println();

//        The deadline for registration and sending photos
        admin.closeApplicationSession();

//        PG2 didn't get into deadline
        photographers.get(2).sendPhoto();
//        PG4 cannot register after the deadline
        photographers.get(4).register(photoContest);
//        PG6 can be created, but cannot register and send photo after the deadline
        photographers.add(new Photographer());
        photographers.get(6).register(photoContest);
        photographers.get(6).sendPhoto();

        System.out.println();
        admin.peerReviewSession();

        System.out.println();
        admin.votingSession();

        System.out.println();
        admin.chooseWinner();

        PhotoContest photoContest1 = admin.createNewContest("ZMIY");
        System.out.println(photoContest1.contestState);
    }
}
