package ru.nedovizin.vvorders;

import android.content.Context;

import java.util.List;

import ru.nedovizin.vvorders.models.Address;
import ru.nedovizin.vvorders.models.ClientLab;

/** Вспомогательный класс для подбора адресов
 *
 */
public class AddressAutoCompleteAdapter extends AutoCompleteAdapter<Address> {

    public AddressAutoCompleteAdapter(Context context) {
        super(context);
    }

    @Override
    public String getNameEntity(Address address) {
        return address.name;
    }

    @Override
    public List<Address> findEntities(String addressTitle) {
        ClientLab clientLab = ClientLab.get(getContext());
        List<Address> addresses = clientLab.getAddressesByLikeName(addressTitle);
        return addresses;
    }
}
