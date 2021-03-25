
$(()=>{
    $(".window.shopadmin .newProduct > form").submit(e => e.preventDefault())
    $(".window.shopadmin .products .new").click(()=>openProduct())
    $(".window.shopadmin .newProduct .cancel").click(()=>$(".window.shopadmin .newProduct").css("display", "none"))
})

function openProduct(){
    $(".window.shopadmin .newProduct .name input").val("")
    $(".window.shopadmin .newProduct .type input").val("")
    $(".window.shopadmin .newProduct .code input").val("")
    $(".window.shopadmin .newProduct").css("display", "block")
}