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
        System.out.println("Contest '"+photoContest.topic+"' is closed.");
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
        System.out.println("Contest '"+photoContest.topic+"' is closed.");
    }
}

/**
 * Class represents a singular photo contest
 */
class PhotoContest {
    ContestState contestState;
    private final ArrayList<Subscriber<ContestState>> photographersList = new ArrayList<>();
    int winnerRate;
    String topic;

    PhotoContest(String name) {
        topic = name;
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
    public ArrayList<Subscriber<ContestState>> getPhotographersList() {
        return photographersList;
    }

    /**
     * Automatic notification for subscribers
     */
    public void notification() {

        //For each subscriber from list of photographers notification is sent
        for (Subscriber<ContestState> contest :
                photographersList) {
            contest.notification(contestState);
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
     * Transition in case of failure. Transitions: 3, 5, 6
     *
     * @param photographer - concrete photographer
     */
    void failed(Photographer photographer);

    /**
     * Transition in case of approval. Transitions: 1, 2, 4, 7, 8, 9
     *
     * @param photographer - concrete photographer
     */
    void accepted(Photographer photographer);

}

/**
 * Photographer state represents his/her failure of contest
 */
class PhotographerFailure implements PhotographerState {

    /**
     * Nothing happens in case of failure
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void failed(Photographer photographer) {

    }

    /**
     * Returns to Initial state in case of acceptance
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new PhotographerInitial());
    }
}

/**
 * Photographer state represents arbitrary photographer not associated with the competition
 */
class PhotographerInitial implements PhotographerState {

    /**
     * Nothing happens in case of failure
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void failed(Photographer photographer) {

    }

    /**
     * Switches to Registration state in case of acceptance
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new PhotographerRegistration());
    }
}

/**
 * Photographer state represents photographers registered to the contest
 */
class PhotographerRegistration implements PhotographerState {

    /**
     * Switches to Failure state in case of failure
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void failed(Photographer photographer) {
        photographer.setState(new PhotographerFailure());
    }

    /**
     * Switches to Application state in case of acceptance
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new PhotographerApplication());
    }
}

/**
 * Photographer state represents photographers on plagiarism checking stage
 */
class PhotographerApplication implements PhotographerState {

    /**
     * Switches to Failure state in case of failure
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void failed(Photographer photographer) {
        photographer.setState(new PhotographerFailure());
    }

    /**
     * Switches to Promoted state in case of acceptance
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new PhotographerPromoted());
    }
}

/**
 * Photographer state represents photographers registered to the contest
 */
class PhotographerPromoted implements PhotographerState {

    /**
     * Switches to Failure state in case of failure
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void failed(Photographer photographer) {
        photographer.setState(new PhotographerFailure());
    }

    /**
     * Switches to Winner state in case of acceptance
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new PhotographerWinner());
    }
}

class PhotographerWinner implements PhotographerState {

    /**
     * Nothing happens in case of failure
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void failed(Photographer photographer) {

    }

    /**
     * Switches to Initial state in case of acceptance
     *
     * @param photographer - concrete photographer
     */
    @Override
    public void accepted(Photographer photographer) {
        photographer.setState(new PhotographerInitial());
    }
}

/**
 * Interface for Observer
 *
 * @param <T> - parameter on which notifications depend
 */
interface Subscriber<T> {
    /**
     * Sends notification to special photographers
     *
     * @param t - parameter on which notifications depend
     */
    void notification(T t);
}

/**
 * Class represents a photographer
 */
class Photographer implements Subscriber<ContestState> {
    private PhotographerState state;
    String name;
    String photo;
    String email;
    String phoneNumber;
    String notifyData;
    boolean accepted;
    int rate;

    /**
     * Primal constructor
     *
     * @param name - name of photographer
     */
    private Photographer(String name) {
        this.state = new PhotographerInitial();
        this.name = name;
        photo = null;
        accepted = false;
        rate = 0;
    }

    /**
     * Constructor with name and phone number or email
     * Could be used only by the programmer-user of system, so no checking for correctness of phone number or email
     *
     * @param name - name of photographer
     * @param contact - how to contact with photographer (by email or phone number)
     */
    Photographer(String name, String contact) {
        this(name);
        if (contact.contains("@"))
            email = contact;
        else
            phoneNumber = contact;
        setNotification();
        System.out.println(notifyData + "You have been successfully discovered as a photographer");
    }

    /**
     * Constructor with name, phone number and email
     * Could be used only by the programmer-user of system, so no checking for correctness of phone number and email
     *
     * @param name - name of photographer
     * @param mail - how to contact with photographer by email
     * @param phone- how to contact with photographer by phone number
     */
    Photographer(String name, String mail, String phone) {
        this(name);
        email = mail;
        phoneNumber = phone;
        setNotification();
        System.out.println(notifyData + "You have been successfully discovered as a photographer");
    }

    /**
     * Constructor with input from console
     */
    Photographer() {
        this(null);
        System.out.println("Hello, new photographer!");
        Scanner scan = new Scanner(System.in);
        //Any name
        while (name == null) {
            System.out.println("Please, enter your name:");
            name = scan.nextLine();
        }
        System.out.println(name + ", how to contact with you? 1/2/3");
        System.out.println("1 - email, 2 - phone, 3 - both email and phone.");
        String line;
        //Accept only  1, 2 or 3 to choose how to contact
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
        //To create full contacting with photographer
        setNotification();
        System.out.println(notifyData + "You have been successfully discovered as a photographer");
    }

    /**
     * Setting email, using Scanner
     *
     * @param scan - Scanner object
     */
    private void setEmail(Scanner scan) {
        System.out.println("Now, enter your email: ");
        while (!setEmail(scan.nextLine())) {
            System.out.println("Try to enter your email again, please: ");
        }
    }

    /**
     * Setting phone number, using Scanner
     *
     * @param scan - Scanner object
     */
    private void setPhone(Scanner scan) {
        System.out.println("Lets set your phone number: ");
        while (!setPhoneNumber(scan.nextLine())) {
            System.out.println("Try to enter your phone number again, please: ");
        }
    }

    /**
     * Inputting phone number and checking for validity
     *
     * @param newPhone - String phone number
     * @return true, if number is valid; false, otherwise
     */
    private boolean setPhoneNumber(String newPhone) {
        //Not empty
        if (newPhone.equals("")) {
            System.out.println("Oh, you didn't enter your phone number!");
            return false;
        } else {
            phoneNumber = "";
            //Only numbers, special symbols and length not more than 11 symbols
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
        //Exactly 11 digits in number
        if (phoneNumber.length() != 11) {
            System.out.println("Phone number have to have exactly 11 digits");
            return false;
        }
        return true;
    }

    /**
     * Inputting email and checking for validity
     *
     * @param newMail - String email
     * @return true, if email is valid; false, otherwise
     */
    private boolean setEmail(String newMail) {
        //Not empty
        if (newMail.equals("")) {
            System.out.println("Oh, you didn't enter your email!");
            return false;
        }
        //Starts with letter
        else if (!"abcdefghijklmnopqrstuvwxyz".contains(newMail.toLowerCase().substring(0, 1))) {
            System.out.println("First letter have to start with the english character");
            return false;
        }
        //Must contain @
        else if (!newMail.contains("@")) {
            System.out.println("Email have to have an @");
            return false;
        }
        //Should be "english_text_and_digits@english_text.english_text"

        else {
            String[] st = newMail.split("@");
            if (st.length < 2 || st[1].length() < 3) {
                System.out.println("Email have to have at least one english letter before '.' and one after");
                return false;
            } else if (!st[1].contains(".")) {
                System.out.println("Email have to have an . after the @ sign");
                return false;
            }
            for (int i = 0; i < st[0].length(); i++) {
                if (!"abcdefghijklmnopqrstuvwxyz1234567890_.".contains(newMail.substring(i, i + 1))) {
                    System.out.println("You can use only english letters, digits, '.' or '_' before the @ sign");
                    return false;
                }
            }
            for (int i = st[0].length() + 1; i < newMail.indexOf('.'); i++) {
                if (!"abcdefghijklmnopqrstuvwxyz".contains(newMail.substring(i, i + 1))) {
                    System.out.println("You can use only english letters and only one . after the @ sign");
                    return false;
                }
            }
            for (int i = newMail.indexOf('.') + 1; i < newMail.length() - 1; i++) {
                if (!"abcdefghijklmnopqrstuvwxyz".contains(newMail.substring(i, i + 1))) {
                    System.out.println("You can use only english letters or '.' after the @ sign");
                    return false;
                }
            }
            if (!"abcdefghijklmnopqrstuvwxyz".contains(newMail.substring(newMail.length() - 1))) {
                System.out.println("You can use only english letters and only one '.' after the @ sign");
                return false;
            }
        }
        email = newMail;
        return true;
    }

    /**
     * Create form of notifications
     */
    private void setNotification() {
        //Name + email/number of phone
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

    /**
     * Registration for the contest
     * Change state and subscribe
     *
     * @param photoContest - concrete photo contest
     */
    public void register(PhotoContest photoContest) {
        if (photoContest.contestState instanceof ContestApplication) {
            System.out.println(this.notifyData+"You successfully registered.");
            photoContest.subscribe(this);
        } else {
            System.out.println(this.notifyData+"You cannot register for the contest.");
        }
    }

    /**
     * Sending a photo to the contest
     * Only Registered photographers can do it
     * Can be done only by programmer-users
     *
     * @param photo - name of the photo
     */
    public void sendPhoto(String photo) {
        //Photographer should be registered
        if (state instanceof PhotographerRegistration) {
            this.photo = photo;
            //Transition to Application state
            accepted();
            System.out.println(notifyData + "You successfully send a photo '" +this.photo+"'.");
        } else {
            System.out.println(notifyData + "You cannot submit a photo.");
        }

    }

    /**
     * Sending a photo to the contest through the console
     * Only Registered photographers can do it
     */
    public void sendPhoto() {
        //Photographer should be registered
        if (state instanceof PhotographerRegistration) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Please, " + name + ", enter name of the photo:");
            this.photo = scan.nextLine();
            //Transition to Application state
            accepted();
            System.out.println(notifyData + "You have successfully sent a photo.");
        } else {
            System.out.println(notifyData + "You cannot submit a photo.");
        }

    }

    public PhotographerState getState() {
        return state;
    }

    public void setState(PhotographerState state) {
        this.state = state;
    }

    /**
     * Failed transition
     */
    public void failed() {
        this.state.failed(this);
    }

    /**
     * Accepted transition
     */
    public void accepted() {
        this.state.accepted(this);
    }


    /**
     * Send notification according to conditions
     *
     * @param contestState - concrete contest
     */
    @Override
    public void notification(ContestState contestState) {
        //In case of Plagiarism checking
        if (contestState instanceof ContestChoice) {
            //Sent a photo
            if (photo == null) {
                failed();
                System.out.println(notifyData + "You didn't submit a photo on time. You failed the contest.");
            }
            //Did not send a photo
            else {
                System.out.println(notifyData + "Your submission is on review.");
            }
        }
        //In case of Voting
        else if (contestState instanceof ContestVote) {
            //Only photographers who sent a photo
            if (state instanceof PhotographerApplication) {
                //Photo was accepted
                if (accepted) {
                    accepted();
                    System.out.println(notifyData + "Your photo was accepted for voting.");
                }
                //Photo was declined
                else {
                    failed();
                    System.out.println(notifyData + "You didn't pass the review session.");
                }
            }
        }
        //In case of Awarding
        else if (contestState instanceof ContestAwarding) {
            //Photographer in Promoted state
            if (state instanceof PhotographerPromoted) {
                System.out.println(notifyData + "Your rate is " + rate + ".");
            }
            //Photographer in Winner state
            else if (state instanceof PhotographerWinner) {
                System.out.println(name + " is the winner!");
                accepted();
            }
            //Photographer in Failure state
            else if (state instanceof PhotographerFailure){
                accepted();
            }
        }
    }
}

/**
 * Organize contest, check for plagiarism, choose winner by console voting
 */
class Admin {
    private PhotoContest photoContest;
    String topic;
    ArrayList<Subscriber<ContestState>> photographersList;

    /**
     * Creates new contest
     *
     * @param topic - String topic of the contest
     * @return new contest
     */
    PhotoContest createNewContest(String topic) {
        System.out.println("New contest about '" + topic + "' is opened.");
        photoContest = new PhotoContest(topic);
        this.topic = topic;
        return photoContest;
    }

    /**
     * Close session for getting photos to the contest
     */
    public void closeApplicationSession() {
        if (photoContest.contestState instanceof ContestApplication) {
            System.out.println("Application session for contest '" + topic + "' is closed");
            //Next stage of contest
            photoContest.deadline();
            //Notify
            photoContest.notification();
        }
    }

    /**
     * Plagiarism checking session
     */
    public void peerReviewSession() {
        photographersList = photoContest.getPhotographersList();
        for (Object photographer :
                photographersList) {
            if (photographer instanceof Photographer && ((Photographer) photographer).getState() instanceof PhotographerApplication) {
                //If photographer went over plagiarism
                ((Photographer) photographer).accepted = plagiarismChecker((Photographer) photographer);
            }
        }
        //Next stage of contest
        photoContest.deadline();
        //Notify
        photoContest.notification();
    }

    /**
     * Searching for photo with the same name
     *
     * @param photographerOnChecking - concrete photographer
     * @return true, if photographer is accepted for contest; false otherwise
     */
    private boolean plagiarismChecker(Photographer photographerOnChecking) {
        for (Object photographer : photographersList) {
            if (!photographer.equals(photographerOnChecking) && photographer instanceof Photographer && (((Photographer) photographer).getState() instanceof PhotographerApplication || ((Photographer) photographer).getState() instanceof PhotographerPromoted)) {
                if (((Photographer) photographer).photo.equals(photographerOnChecking.photo)) {
                    photographerOnChecking.accepted = false;
                    ((Photographer) photographer).accepted = false;
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Inputting number of votes for each photographer
     */
    public void votingSession() {
        photoContest.winnerRate = 0;
        boolean isFirst = true;

        for (Object photographer : photographersList) {
            if (photographer instanceof Photographer && ((Photographer) photographer).getState() instanceof PhotographerPromoted) {
                if (isFirst) {
                    System.out.println("Now we will vote to choose the best one!");
                    isFirst = false;
                }
                //Set number for rate
                int rate = setRating((Photographer) photographer);
                ((Photographer) photographer).rate = rate;
                if (rate > photoContest.winnerRate) {
                    photoContest.winnerRate = rate;
                }
            }
        }
        //Next stage of contest
        photoContest.deadline();
        //Тщешан
        photoContest.notification();
    }

    /**
     * Inputting rating number
     *
     * @param photographer - concrete photographer
     * @return number of rate
     */
    private int setRating(Photographer photographer) {
        System.out.println("How many likes does " + photographer.photo + " have?");
        int rate = 0;
        Scanner scan = new Scanner(System.in);
        try {
            rate = Integer.parseInt(scan.nextLine());
        } catch (NumberFormatException exc) {
            System.out.println("Accepted only integers. Rating for " + photographer.photo + " is 0.");
        }
        return rate;
    }

    /**
     * Winner choosing procedure
     */
    public void chooseWinner() {
        //Should have more than 0 votes
        if (photoContest.winnerRate!= 0){
            for (Object photographer : photographersList) {
                if (photographer instanceof Photographer && ((Photographer) photographer).getState() instanceof PhotographerPromoted) {
                    if (((Photographer) photographer).rate == photoContest.winnerRate) {
                        //Next state for any photographer with max number of likes
                        ((Photographer) photographer).accepted();
                    } else {
                        //Fail otherwise
                        ((Photographer) photographer).failed();
                    }
                }
            }
            //Notification for all, who participated at contest
            photoContest.notification();
        } else {
            System.out.println("Unfortunately, no one won the contest.");
        }
        //Next stage of contest
        photoContest.deadline();
    }
}

/**
 * Example of system usage
 * Some photographers should be created
 */
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
        photographers.get(5).sendPhoto("PhotoWithPlagiarism");
        photographers.get(1).sendPhoto("PhotoWithPlagiarism");
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
        System.out.println(photographers.get(0).getState());
        System.out.println(photographers.get(2).getState());
        admin.chooseWinner();
        System.out.println(photographers.get(0).getState());
        System.out.println(photographers.get(2).getState());
    }
}
