//am ales sa folosesc bibliotecile de mai jos pentru a eficientiza procesul de extragere continut, lucrul cu date, importuri si dictionare
using System;
using System.Collections.Generic;
using System.IO;
using HtmlAgilityPack;

class Program
{
    static void Main()
    {
// se citeste folderul 
        string path = "C:\\clones"; 

// se obtin fisierele din array
        string[] files = Directory.GetFiles(path, "*.html");
// am creeat un dictionar pentru a stoca fisierele si textul fara tagurile HTML
        Dictionary<string, string> fileTexts = new Dictionary<string, string>();
        // se parcurg prin foreach toate fisierele din lista 
        foreach (string file in files)
        {
            // am ales sa citesc continutul fiecarui fisier HTML ca un sir de caractere
            string content = File.ReadAllText(file);
            //am folosit functia RemoveHtmlTags pentru eliminarea tag-urilor din sintaxa
            string text = ExtractText(content);
            //apoi am adaugat fisierul si textul in dictionarul creeat anterior
            fileTexts[file] = text;
        }
// am apelat functia declarata mai jos pentru a grupa fisierele asemanatoare 
        List<List<string>> groups = GroupSimilarFiles(fileTexts);
// apoi am creeat un index pentru a le numara 
        int index = 1;
        //apoi asemenator ca mai sus, am parcurs fiecare grup de fisiere din lista grupurilor definite de groups 
        foreach (var group in groups)
        {
            //apoi am afisat in consola fiecare grup + index + fisierele din grup
            Console.WriteLine("Grupul {groupIndex}: {string.Join(", ", group)}");
            // si am indexat grupul urmator
            index++;
        }
    
    }
    static string ExtractText(string htmlsintax)
    {
        // creaza un nou documnet html stocat in variabila htmlfile
        var htmlfile = new HtmlDocument();
        //apoi se incarca continutul fisierului
        htmlfile.LoadHtml(htmlsintax); 
        //functia intoarce textul fisierului
        return htmlfile.DocumentNode.InnerText; 
    }

        static List<List<string>> GroupSimilarFiles(Dictionary<string, string> files)
    {
        // am creat o lista pentru a stoca grupurile de fisiere ca un string
        List<List<string>> groups = new List<List<string>>();
        // apoi pentru a nu se procesa aceleasi grupuri de 2 ori, am facut o evidenta pentru fisierele care au fost deja procesare
        HashSet<string> processed = new HashSet<string>();
        // am parcurs fiecare fisier din dictionar
        foreach (var fileA in files)
        {
            //daca fisierul s-a procesat si este gasit inca o data, bucla trece la urmatorul
            if (processed.Contains(fileA.Key)) continue;
            //se creaza un nou grup cu fisierul 0
            List<string> group = new List<string> { fileA.Key };
            processed.Add(fileA.Key);
            // se parcurg toate fisierele inca o data pentru a compara restul fisierelor cu primul de mai sus
            // apoi se verifica daca fisierele contin string-uri similare prin aplearea functiei definita mai jos
            // daca se gasete fisere similare se adauga fisierul procesat curent la lista de grupuri
            foreach (var fileB in files)
            {
                if (fileA.Key != fileB.Key)
                {
                    if (AreSimilar(fileA.Value, fileB.Value))
                    {
                        group.Add(fileB.Key);
                        processed.Add(fileB.Key);
                    }
                }
            }
            groups.Add(group);
        }
        return groups;
    }

        

// functiei de comparare similitudini de stringuri ii se dau 2 argumente (se compara 2 siruri)
//am impartit textele in cuvinte prin .Split(). text1.Split() si text2.Split() intoarce un array de cuvinte ce alcatuiesc textele
//apoi am folosit .Intersect() pentru a compara fiecare array de cuvinte si a intoarce cuvintele comune din cele 2 array-uri  
//si .Count() pentu a numara cuvintele comune
// stocare in commonWords

//pentru a trata cel mai lung text am folosit Math.Max()
//se splituiesc cele 2 texte si se alege cel mai lung
//stocare in totalWords


//apoi am impartit numărul de cuvinte comune la numărul total de cuvinte. 
//Rezultatul poate fi intre 0 și 1, si reprezintă proportia de cuvinte comune in raport cu totalul
    static bool AreSimilar(string text1, string text2)
    {
        int commonWords = text1.Split().Intersect(text2.Split()).Count();
        int totalWords = Math.Max(text1.Split().Length, text2.Split().Length);
        return (double)commonWords / totalWords > 0.5; 
    }
}

// va fi true daca textele comparate sunt similare (mai mult de 50+0.5% se afla in ambele texte)
// va fi false daca textele contin mai putin de 50% din totalul cuvintelor comune comparate

