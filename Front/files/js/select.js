$(()=>{
    let postalcode_input = $(".window.home .selectCity .postalcode input")
    let select = $(".window.home .selectCity .city select")


    let ccity = getCookie("city")
    let cpostalcode = getCookie("postalcode")

    if(ccity !== "" && cpostalcode !== ""){
        city = ccity
        postalcode = cpostalcode
    }

    postalcode_input.val(postalcode)
    setCity(postalcode, city, select)
    actStores()
    $("header .address span").html(city)


    setCitySelect(postalcode_input, select, actCities)

    $(".window.home .selectCity").submit(e=>e.preventDefault())
    select.change(()=>{
        actCities()
    })

    function actCities(){
        let pc = $(".window.home .selectCity .postalcode input").val()
        let city_ = $(".window.home .selectCity .city select").val()

        clearInputError()

        if(pc === "")
            inputError("postalcode")
        else if(city_ === "")
            inputError("city")
        else{
            postalcode = pc
            city = city_
            $("header .address span").html(city)
            actStores()
            var d = new Date();
            d.setTime(d.getTime() + (15*24*60*60*1000));
            var expires = d.toUTCString();
            setCookie("city", city, expires)
            setCookie("postalcode", postalcode, expires)
        }
    }
    $("header .address").click(()=>{
        openWindow("home")
        $(".window.home .selectCity .postalcode input").val(postalcode)
        setCity(postalcode, city, select)
    })
})

function setCitySelect(postalcode_input, select_input, f = null){
    postalcode_input.on("input", (e)=>{
        let value = postalcode_input.val()
        searchCity(value, select_input, f)
    })
}
function setCity(pc, name, output){
    searchCity(pc, output, ()=>{
        output.val(name)
    })
}
function searchCity(postalcode, output, f){
    if(postalcode.length === 5){
        $.get("https://datanova.legroupe.laposte.fr/api/records/1.0/search/?dataset=laposte_hexasmal&q="+postalcode+"&facet=code_commune_insee&facet=nom_de_la_commune&facet=code_postal&facet=ligne_5",{},data=>{
            output.html("")

            if(data.facet_groups){
                let cities = data.facet_groups[2].facets

                for(let i=0; i<cities.length; i++){
                    let city = cities[i]
                    let name = city.name

                    output.append($("<option value='"+name+"'></option>").html(name))

                    if(output.val() == "")
                        output.val(name)

                }
                if(f)
                    f()
            }

        }, "json")
    }else{
        output.html("")
    }
}