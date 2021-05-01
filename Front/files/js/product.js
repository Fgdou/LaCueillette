let product3 = null

function actProduct(product_){
    openWindow("product")

    product3 = product_

    let pname = $(".window.product .infos .name")
    let pprice = $(".window.product .infos .price")
    let pdescription = $(".window.product .description")
    let pquantity = $(".window.product .infos .quantity")
    let pclick = $(".window.product .infos .logos .clickandcollect")
    let pdelivered = $(".window.product .infos .delivered")

    let sname = $(".window.product .shop .name")
    let saddress = $(".window.product .shop .address")

    let subproducts = $(".window.product .subproducts")
    let list = $(".window.product .subproducts .list")

    pname.html(product_.name)
    pprice.html(getPrice(product_))
    pquantity.html(getQuantity(product_))
    pclick.prop("hidden", !product_.canBePicked)
    pdelivered.prop("hidden", true)
    pdescription.html(product_.description)

    sname.addClass("clickable").html(product_.store.name)
    saddress.html(getAddress(product_.store.address))

    sname.click(()=>openStore(product_.store))



    $.post(api + "subproduct/get", {
        product_id: product_.id
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            $(".window.product .subproducts .list *").off()
            list.html("")
            for(let i=0; i<data.length; i++){
                let sp = data[i]

                let e = $("<tr></tr>")

                let name = $("<td></td>").html(sp.special_tag)
                let quantity = $("<td></td>").html(sp.quantity + ((sp.product.priceKg)?" g":""))
                let select = $("<select></select>").addClass("dropDown")
                let button = $("<button class='clickable button' disabled>Ajouter au panier</button>")

                fillSelect(select, sp.quantity)

                e.append(name)
                e.append(quantity)

                if(sp.product.canBePicked) {
                    e.append($("<td></td>").addClass("number").append(select))
                    e.append($("<td></td>").append(button))
                }

                select.change(()=>{
                    button.prop("disabled", select.val() == 0)
                })

                button.click(()=>{
                    let q = select.val()
                    let id = sp.id
                    addToCart(q, id)
                })

                list.append(e)
            }
        }
    }, "json")
}
function addToCart(quantity, id){
    if(user === null){
        openWindow("login")
        return
    }

    $.post(api + "cart/add", {
        user_token: token,
        subproduct_id: id,
        quantity: quantity
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            actCart()
            successPopup("Produit ajouté au Panier")
        }
    }, "json")
}
function getQuantity(product){
    if(!product.priceKg)
        return product.numberSubproducts + " restant"
    else
        return product.numberSubproducts/1000 + " kg restant"
}
function getSubQuantity(product, sub){
    if(!product.priceKg)
        return sub.quantity
    else
        return sub.quantity/1000 + " kg"
}
function fillSelect(select, n){
    select.html("")
    for(let i=0; i<=n; i++)
        select.append($("<option></option>").html(i))
}