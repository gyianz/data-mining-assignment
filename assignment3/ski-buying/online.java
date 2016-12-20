import java.util.*;

public class online {

    public static void main(String[] args)
    {
        int RentCost = 25;
        int BuyCost = 200;
        int compet_ratio = 0;

        int rent_day = Integer.parseInt(args[0]); // the days you will end up for rent the ski.

        if(rent_day <= 0) {
            System.exit(0);
        }        

        int opt = RentCost * rent_day;

        System.out.println("The offline optimum cost is " + opt);

        int C = RentCost * rent_day + BuyCost;

        System.out.println("The actual cost is " + C);


        compet_ratio = C / opt;


        System.out.println("competitive ratio is " + compet_ratio);
        System.out.println("rent for " + compet_ratio + " days, then buy");

    }
}
