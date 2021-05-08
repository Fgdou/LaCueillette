function openOrder(order, admin = false){
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

    $(".window.order .total .ht").html("Total HT : " + order.priceHT.toFixed(2) + " €")
    $(".window.order .total .ttc").html("Total TTC : " + order.priceTTC.toFixed(2) + " €")

    let btns = $(".window.order .commands")
    let btnstart = $(".window.order .commands .start")
    let btnfinish = $(".window.order .commands .finish")

    btnstart.off()
    btnfinish.off()

    if(admin){
        btns.css("display", "grid")
        if(order.state === 0){
            btnstart.css("display", "inline-block")
            btnfinish.css("display", "none")
        }else if(order.state === 1){
            btnfinish.css("display", "inline-block")
            btnstart.css("display", "none")
        }else{
            btnstart.css("display", "none")
            btnfinish.css("display", "none")
        }

        btnstart.click(()=>{
            $.post(api + "order/startPrepare", {
                user_token: token,
                order_id: order.id
            }, data=>{
                if(data.error)
                    errorPopup(data.error)
                else {
                    btnfinish.css("display", "inline-block")
                    btnstart.css("display", "none")
                }
            }, "json")
        })
        btnfinish.click(()=>{
            $.post(api + "order/FinishPrepare", {
                user_token: token,
                order_id: order.id
            }, data=>{
                if(data.error)
                    errorPopup(data.error)
                else
                    shopAdminAct(shop)
            }, "json")
        })
    }else{
        btns.css("display", "none")
    }
}

function getState(state){
    if(state === 0)
        return "En attente"
    if(state === 1)
        return "En préparation"
    if(state === 2)
        return "Prête"
    if(state === -1)
        return "Annulée"
}