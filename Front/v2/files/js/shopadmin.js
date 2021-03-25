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
    let price_kg = $(".window.newProduct .price_kg input").val()
    let picked = $(".window.newProduct .picked input").val()
    let delivered = $(".window.newProduct .delivered input").val()

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
}
function parseProductsShop(data){
    let list = $(".window.shopadmin .products .list")
    list.html("")

    for(let i=0; i<data.length; i++){
        let product = data[i]

        let tr = $("<tr></tr>")

        tr.append($("<td></td>").addClass("name").html(product.name))
        tr.append($("<td></td>").addClass("quantity").html(product.numberSubproducts))

        let btns = $("<td></td>")

        let edit = $("<img src='files/img/edit.svg' alt='edit'>").addClass("edit clickable")
        let remove = $("<img src='files/img/delete.svg' alt='delete'>").addClass("delete clickable")

        edit.click(()=>{
            openProduct(product)
        })
        remove.click(()=>{
            deleteProduct(product)
        })

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
        $(".window.newProduct .price_kg input").val(false)
        $(".window.newProduct .picked input").val(false)
        $(".window.newProduct .delivered input").val(false)
    }else{
        listProductCategoryAct(()=>{
            $(".window.newProduct .type select").val(product.category.id)
        })
        $(".window.newProduct .name input").val(product.name)
        $(".window.newProduct .price input").val(product.price)
        $(".window.newProduct .tva input").val(product.price)
        $(".window.newProduct .description input").val(product.description)
        $(".window.newProduct .price_kg input").val(product.price_kg)
        $(".window.newProduct .picked input").val(product.canBePicked)
        $(".window.newProduct .delivered input").val(product.canBeDelivered)
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