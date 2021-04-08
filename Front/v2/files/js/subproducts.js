let product2 = null
let subproduct = null

$(()=>{
    $(".window.subproducts .new").click(()=>{
        showNewSubproduct(null)
    })
    $(".window.newSubproduct form").submit(e => e.preventDefault())
    $(".window.newSubproduct .submit").click(()=>updateSubproduct(null))
    $(".window.newSubproduct .cancel").click(()=>$(".window.newSubproduct").css("display", "none"))
})

function actSubproducts(product_){
    product2 = product_

    $.post(api + "subproduct/get", {
        product_id: product2.id
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else
            parseSubProducts(data)
    }, "json")
}
function parseSubProducts(data){
    let list = $(".window.subproducts .list")

    list.html("")

    for(let i=0; i<data.length; i++){
        let sp = data[i]

        let tr = $("<tr></tr>")

        tr.append($("<td></td>").addClass("quantity").html(sp.quantity))
        tr.append($("<td></td>").addClass("tag").html(sp.special_tag))

        let buttons = $("<td></td>").addClass("actions")

        let edit = $("<img src='files/img/edit.svg' alt='edit'>").addClass("clickable edit")
        let remove = $("<img src='files/img/delete.svg' alt='delete'>").addClass("clickable remove")

        edit.click(()=>showNewSubproduct(sp))
        remove.click(()=>removeSubproduct(sp))

        buttons.append(edit)
        buttons.append(remove)

        tr.append(buttons)

        list.append(tr)
    }
}
function removeSubproduct(subproduct){
    $.post(api + "subproduct/delete", {
        user_token: token,
        subproduct_id: subproduct.id
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else
            actSubproducts(product2)
    }, "json")
}
function showNewSubproduct(subproduct_){
    subproduct = subproduct_

    let tag = $(".window.newSubproduct .tag input")
    let quantity = $(".window.newSubproduct .quantity input")
    let name = $(".window.newSubproduct .name")

    name.html(product2.name)

    if(subproduct === null){
        tag.val("")
        quantity.val("")
    }else{
        tag.val(subproduct.special_tag)
        quantity.val(subproduct.quantity)
    }

    $(".window.newSubproduct").css("display", "grid")
}
function updateSubproduct(){

    let tag = $(".window.newSubproduct .tag input").val()
    let quantity = $(".window.newSubproduct .quantity input").val()

    clearInputError()

    if(tag === "")
        inputError("tag")
    else if(quantity === "")
        inputError("quantity")
    else{
        if(subproduct === null){
            $.post(api + "subproduct/new", {
                user_token: token,
                tag: tag,
                quantity: quantity,
                product_id: product2.id
            }, data=>{
                if(data.error)
                    errorPopup(data.error)
                else{
                    $(".window.newSubproduct").css("display", "none")
                    actSubproducts(product2)
                }
            }, "json")
        }else{
            $.post(api + "subproduct/modify", {
                user_token: token,
                tag: tag,
                quantity: quantity,
                subproduct_id: subproduct.id
            }, data=>{
                if(data.error)
                    errorPopup(data.error)
                else{
                    $(".window.newSubproduct").css("display", "none")
                    actSubproducts(product2)
                }
            }, "json")
        }
    }
}