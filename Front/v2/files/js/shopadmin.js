
$(()=>{
    $(".window.newProduct > form").submit(e => e.preventDefault())
    $(".window.shopadmin .products .new").click(()=>openProduct())
    $(".window.newProduct .cancel").click(()=>$(".window.newProduct").css("display", "none"))
})

function shopAdminAct(shop){

}
function openProduct(){
    $(".window.newProduct .name input").val("")
    $(".window.newProduct .type input").val("")
    $(".window.newProduct .code input").val("")
    $(".window.newProduct").css("display", "block")
}