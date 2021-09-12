package ru.nedovizin.vvorders;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.nedovizin.vvorders.models.ClientLab;
import ru.nedovizin.vvorders.models.Product;

/** Вспомогательный класс для подбора продуктов
 *
 */
public class ProductAutoCompleteAdapter extends AutoCompleteAdapter<ProductItem> {

    public ProductAutoCompleteAdapter(Context context) {
        super(context);
    }

    @Override
    public String getNameEntity(ProductItem productItem) {
        return productItem.product.name;
    }

    @Override
    public List<ProductItem> findEntities(String productTitle) {
        List<Product> products = new ArrayList<>();
        List<ProductItem> productItems = new ArrayList<>();
        ClientLab clientLab = ClientLab.get(getContext());
        String quantity = "1";

        String product = productTitle.trim()
                .toLowerCase()
                .replace(" м ", " ( ");
        String[] words = product.split(" ");
        int idxEndWord = words.length - 1;
        if (idxEndWord > 0 && words[idxEndWord].matches("\\d*")) {
            // Если последнее слово - число и оно не первое
            quantity = words[idxEndWord];
            words = Arrays.copyOfRange(words, 0, idxEndWord);
            product = TextUtils.join(" ", words);
            products = clientLab.getProductsByLikeWords(product);
        }
        for (Product prod : products) {
            ProductItem prodItem = new ProductItem(prod);
            prodItem.quantity = quantity;
            productItems.add(prodItem);
        }
        return productItems;
    }
}
