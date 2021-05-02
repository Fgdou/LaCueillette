let product = null
let shop = null

$(()=>{
    $(".window.newProduct > form").submit(e => e.preventDefault())
    $(".window.newProduct .submit").click(()=>createChangeProduct())
    $(".window.shopadmin .products .new").click(()=>openProduct(null))
    $(".window.newProduct .cancel").click(()=>$(".window.newProduct").css("display", "none"))
    $(".window.newProduct .type select").change(()=>{
        let select = $(".window.newProduct .type select")

        if(select.val() === "new"){
            let name = prompt("Nom de la nouvelle categorie")
            if(name !== null && name !== ""){
                $.post(api + "product/category/new", {
                    user_token: token,
                    store_id: shop.id,
                    name: name,
                    parent_id: null
                }, data=>{
                    if(data.error)
                        errorPopup(data.error)
                    listProductCategoryAct(()=>{
                        select.val(data.id)
                    })
                }, "json")
            }
        }
    })
})

function createChangeProduct(){

    let name = $(".window.newProduct .name input").val()
    let price = $(".window.newProduct .price input").val()
    let tva = $(".window.newProduct .tva input").val()
    let description = $(".window.newProduct .description input").val()

    let category = $(".window.newProduct .type select").val()
    let price_kg = $(".window.newProduct .price_kg input").prop("checked")
    let picked = $(".window.newProduct .picked input").prop("checked")
    let delivered = $(".window.newProduct .delivered input").prop("checked")

    if(product === null){
        $.post(api + "product/new", {
            user_token: token,
            store_id: shop.id,
            name: name,
            price: price,
            tva: tva,
            description: description,
            price_kg: price_kg,
            canBeDelivered: delivered,
            canBePicked: picked,
            time_start: null,
            time_stop: null,
            expiration: null,
            category_id: category
        }, data=>{
            if(data.error){
                errorPopup(data.error)
            }else{
                $(".window.newProduct").css("display", "none")
                shopAdminAct(shop)
            }
        }, "json")
    }else{
        $.post(api + "product/modify", {
            user_token: token,
            product_id: product.id,
            store_id: shop.id,
            name: name,
            price: price,
            tva: tva,
            description: description,
            price_kg: price_kg,
            canBeDelivered: delivered,
            canBePicked: picked,
            time_start: null,
            time_stop: null,
            expiration: null,
            category_id: category
        }, data=>{
            if(data.error){
                errorPopup(data.error)
            }else{
                $(".window.newProduct").css("display", "none")
                shopAdminAct(shop)
            }
        }, "json")
    }
    $(".window.newProduct").css("display", "none")
    shopAdminAct(shop)
}
function shopAdminAct(shop_){
    shop = shop_
    $.post(api + "product/get/byStore", {
        store_id: shop.id
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            parseProductsShop(data)
        }
    }, "json")
    $.post(api + "order/get/byStore", {
        user_token: token,
        store_id: shop_.id
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else
            parseShopOrders(data)
    }, "json")
}
function parseShopOrders(orders){
    let listprep = $(".window.shopadmin .listprep")
    let listwait = $(".window.shopadmin .listwait")
    let listfinish = $(".window.shopadmin .listfinish")


    let nprep_span = $(".window.shopadmin .nprep")
    let nwait_span  = $(".window.shopadmin .nwait")
    let nfinish_span  = $(".window.shopadmin .nfinish")

    let nprep = 0
    let nwait  = 0
    let nfinish = 0

    listprep.html("")
    listwait.html("")
    listfinish.html("")

    for(let i=0; i<orders.length; i++){
        let order = orders[i]

        let tr = $("<tr></tr>")
        tr.append($("<td></td>").html(order.id))
        tr.append($("<td></td>").html(dateToStringFormat(order.created)))
        tr.append($("<td></td>").html(order.user.name + " " + order.user.surname))
        tr.append($("<td></td>").html(order.subProducts.length))

        let buttons = $("<td></td>")

        let cancel = $("<img src='files/img/delete.svg' title='Annuler la commande' class='clickable'>")

        cancel.click(()=>{
            $.post(api + "order/CancelPrepare", {
                user_token: token,
                order_id: order.id
            }, data=>{
                if(data.error)
                    errorPopup(data.error)
                else
                    shopAdminAct(shop)
            }, "json")
        })


        if(order.state === 0){
            nwait++
            listwait.append(tr)
            buttons.append(cancel)
        }else if(order.state === 1){
            nprep++
            listprep.append(tr)
            buttons.append(cancel)
        }else{
            nfinish++
            listfinish.append(tr)
        }
        tr.append(buttons)
    }

    nprep_span.html(nprep)
    nwait_span.html(nwait)
    nfinish_span.html(nfinish)
}
function parseProductsShop(data){
    let list = $(".window.shopadmin .products .list")
    $(".window.shopadmin .products .list *").off()
    list.html("")

    for(let i=0; i<data.length; i++){
        let product = data[i]

        let tr = $("<tr></tr>")

        tr.append($("<td></td>").addClass("name").html(product.name))
        tr.append($("<td></td>").addClass("quantity").html(product.numberSubproducts))

        let btns = $("<td></td>")

        let edit = $("<img src='files/img/edit.svg' alt='edit'>").addClass("edit clickable")
        let remove = $("<img src='files/img/delete.svg' alt='delete'>").addClass("delete clickable")
        let quantity = $("<img src='files/img/stock.svg' alt='edit quantity'>").addClass("quantity clickable")

        edit.click(()=>{
            openProduct(product)
        })
        remove.click(()=>{
            deleteProduct(product)
        })
        quantity.click(()=>{
            openWindow("subproducts")
            actSubproducts(product)
        })

        btns.append(quantity)
        btns.append(edit)
        btns.append(remove)

        tr.append(btns)

        list.append(tr)
    }
}
function deleteProduct(product){
    $.post(api + "product/delete", {
        user_token: token,
        product_id: product.id
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        shopAdminAct(shop)
    }, "json")
}
function openProduct(product_){
    product = product_
    if(product_ === null) {
        $(".window.newProduct .name input").val("")
        $(".window.newProduct .price input").val("")
        $(".window.newProduct .tva input").val("")
        $(".window.newProduct .description input").val("")
        $(".window.newProduct .price_kg input").prop("checked", false)
        $(".window.newProduct .picked input").prop("checked", false)
        $(".window.newProduct .delivered input").prop("checked", false)
        listProductCategoryAct()
    }else{
        listProductCategoryAct(()=>{
            $(".window.newProduct .type select").val(product.category.id)
        })
        $(".window.newProduct .name input").val(product.name)
        $(".window.newProduct .price input").val(product.price)
        $(".window.newProduct .tva input").val(product.tva)
        $(".window.newProduct .description input").val(product.description)
        $(".window.newProduct .price_kg input").prop("checked", product.priceKg)
        $(".window.newProduct .picked input").prop("checked", product.canBePicked)
        $(".window.newProduct .delivered input").prop("checked", product.canBeDelivered)
    }
    $(".window.newProduct").css("display", "block")
}
function listProductCategoryAct(fun){
    $.post(api + "product/category/getAll", {}, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            parseProductCategory(data)
            if(fun)
                fun()
        }
    })
}
function parseProductCategory(data){
    let select = $(".window.newProduct .type select")
    select.html("")

    for(let i=0; i<data.length; i++){
        let cat = data[i]

        let option = $("<option value='"+cat.id+"'></option>").html(cat.name)

        select.append(option)
    }
    select.append($("<option value='new'>Nouveau</option>"))
}