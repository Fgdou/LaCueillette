let user = null
let page = null
let lastPage = "user"
let token = null
let url = "https://lacueillette.ml/v2/"
let api = "https://lacueillette.ml/api/"

$(()=>{
    openWindow(lastPage)

    $("header .account").click(()=>{
        if(user === null)
            openWindow("login")
        else {
            openWindow("user")
            userAct()
        }
    })
    $("header .logo").click(()=>{
        window.location.replace(url)
    })



})

function openWindow(name){
    closeWindows()
    if(name != page)
        lastPage = page
    page = name
    $(".window."+name + " input").val("")
    clearInputError()
    $(".window."+name).css("display", "grid")
}
function closeWindows(){
    $(".window").css("display", "none")
}
function inputError(name){
    $(".input."+name).addClass("error")
    $(".input."+name + " input").focus()
}
function clearInputError(){
    $(".input").removeClass("error")
}
function errorPopup(msg){
    console.log(msg)
}
function dateToString(date){
    return (new Date(date.year, date.month, date.day+1, date.hour, date.minute, date.second)).toString()
}