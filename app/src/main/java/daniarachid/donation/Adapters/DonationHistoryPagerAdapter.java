package daniarachid.donation.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import daniarachid.donation.DonationRequestManagement.history_donated;
import daniarachid.donation.DonationRequestManagement.history_received;

public class DonationHistoryPagerAdapter extends FragmentPagerAdapter {
    private int tabsNumber;
    public DonationHistoryPagerAdapter(@NonNull @NotNull FragmentManager fm, int behavior, int tabsNumber) {
        super(fm, behavior);
        this.tabsNumber = tabsNumber;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                //donated
                return new history_donated();
            case 1:
                return new history_received();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabsNumber;
    }
}
