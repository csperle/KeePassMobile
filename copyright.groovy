def processedFiles = 0
def allDirs = 0

def curDirName = ".";
if (args.length > 0) curDirName = args[0]

File curDir = new File(curDirName)
def input = new Scanner(System.in)
print "current dir is [${curDir.absolutePath}] continue? "
userContinue = input.nextLine()
if (userContinue != 'y') {
	println "aborted by user"
	return
}

def copyrightFileName = "copyright.txt";
if (args.length > 1) copyrightFileName = args[1]

File copyrightFile = new File(copyrightFileName)
if (!copyrightFile.exists()) {
	println "copyright text file [${copyrightFileName}] not available. exit"
	return
}
def copyrightText = copyrightFile.getText("UTF-8")

input = new Scanner(System.in)
println "copyright text to use: \n"
println copyrightText
print "continue? "
userContinue = input.nextLine()
if (userContinue != 'y') {
	println "aborted by user"
	return
}

println "ok, processing files:"
curDir.eachFileRecurse { file ->
	if(!file.isDirectory())
		if(file.path.endsWith(".java")) {
			print "${file.path}..."
			def filecontent = file.getText("UTF-8")
			if(!filecontent.startsWith(copyrightText)) {
				def javacontent = filecontent.substring(filecontent.indexOf("package"),filecontent.size())
				file.withPrintWriter('UTF-8') { PrintWriter out ->
        			out << copyrightText
        			out << javacontent
    			}
				println "changed"
				processedFiles++
			} else println "up to date. skipped"
		}
	else allDirs++
}
println "-> processed ${processedFiles} files in ${allDirs} directories. finished"

