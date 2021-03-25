let user = null
let page = null
let lastPage = "shopadmin"
let token = null
let url = "https://lacueillette.ml/v2/"
let api = "https://lacueillette.ml/api/"

$(()=>{
    setInterval(renewToken, 3600000)

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

    checkAlive()

})

function renewToken(){
    if(user === null)
        return;
    $.post(api + "user/newToken", {
        user_token: token
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            token = data.value
            document.cookie = "token="+data.value + "; expires="+dateToString(data.expiration)
        }
    }, "json")
}

function checkAlive(){
    $.post(api + "alive", {}, data=>{
        if(!data.log || data.log !== "alive") {
            errorPopup("Erreur de communication avec le serveur")
            setTimeout(checkAlive, 6000)
        }else{
            setTimeout(checkAlive, 30000)
        }
    }).fail(()=>{
        errorPopup("Communication impossible avec le serveur")
        setTimeout(checkAlive, 6000)
    })
}

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

    let pop = $(".errorPopup")

    pop.html(msg)
    pop.css("display", "block")

    setTimeout(()=>{
        pop.css("display", "none")
    }, 5000)
}
function dateToString(date){
    return (new Date(date.year, date.month, date.day+1, date.hour, date.minute, date.second)).toString()
}
function numFormat(num, n){
    num = num.toString()

    while(num.length < n)
        num = "0" + num

    return num
}
function dateToStringFormat(date){
    return numFormat(date.day, 2) + "-" + numFormat(date.month, 2) + "-" +numFormat(date.year, 4) + " " + numFormat(date.hour, 2) + ":" + numFormat(date.minute, 2) + ":" + numFormat(date.second, 2)
}