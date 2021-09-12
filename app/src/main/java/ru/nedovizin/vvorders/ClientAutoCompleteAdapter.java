package ru.nedovizin.vvorders;

import android.content.Context;

import java.util.List;

import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Contragent;

/** Вспомогательный класс для подбора контрагентов
 *
 */
public class ClientAutoCompleteAdapter extends AutoCompleteAdapter<Contragent> {

    public ClientAutoCompleteAdapter(Context context) {
        super(context);
    }

    @Override
    public String getNameEntity(Contragent client) {
        return client.name;
    }

    @Override
    public List<Contragent> findEntities(String clientTitle) {
        ClientLab clientLab = ClientLab.get(getContext());
        List<Contragent> clients = clientLab.getClientsByLikeName(clientTitle);
        return clients;
    }
}
