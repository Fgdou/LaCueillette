function openOrder(order){
    openWindow("order")

    $(".window.order .title").html("Commande " + order.id);
    $(".window.order .order .date").html(dateToStringFormat(order.created));
    $(".window.order .order .status").html(order.status);

    $(".window.order .shop .name").html(order.store.name);
    $(".window.order .shop .address").html(getAddress(order.store.address));
    $(".window.order .shop").off()
    $(".window.order .shop").click(()=>{
        openStore(order.store)
    })

    $(".window.order .user .name").html(order.user.name + " " + order.user.surname);
    $(".window.order .user .address").html(getAddress(order.address));

    let list = $(".window.order .list")
    list.html("")
    for(let i=0; i<order.subProducts.length; i++){
        let subproduct = order.subProducts[i]
        let quantity = order.quantities[i]

        let tr = $("<tr></tr>")
        tr.append($("<td></td>").html(subproduct.product.name))
        tr.append($("<td></td>").html(subproduct.special_tag))
        tr.append($("<td></td>").html(quantity))
        tr.append($("<td></td>").html(getPriceOnly(subproduct, quantity)))

        tr.click(()=>actProduct(subproduct.product))

        list.append(tr)
    }

    $(".window.order .total .ht").html(order.priceHT.toFixed(2) + " €")
    $(".window.order .total .tc").html(order.priceTTC.toFixed(2) + " €")

    //TODO add order manipulation
}